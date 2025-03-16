package org.example.software_project.Persistence;

import lombok.extern.slf4j.Slf4j;
import org.example.software_project.business.User;
import org.springframework.stereotype.Component;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserDaoImpl extends MySQLDao implements UserDao {

    public UserDaoImpl() {
        super();
    }

    public UserDaoImpl(Connection conn) {
        super(conn);
    }

    public UserDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }


    @Override
    public boolean register(User user) {
        if (!validateUser(user)) {
            throw new IllegalArgumentException("You must enter a username, first name, last name, password, email, and phone number to proceed.");
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }


        if (user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }


        if (user.getUsername().length() > 20) {
            throw new IllegalArgumentException("Username is too long. Maximum 20 characters.");
        }


        if (isUsernameOrEmailTaken(user.getUsername(), user.getEmail())) {
            return false;
        }
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(user.getPassword(), org.mindrot.jbcrypt.BCrypt.gensalt());
        user.setPassword(hashedPassword);

        String sql = "INSERT INTO Users (username,firstName, secondName, email, password, phone, role, profile_picture) VALUES(?, ?, ?, ?, ?,?,?,?)";
        try (Connection conn = getConnection()) {
            if (conn == null) {
                log.error("Failed to get database connection.");
                return false;
            }


            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // Set the parameters for the SQL query
                ps.setString(1, user.getUsername()); // Name
                ps.setString(2, user.getFirstName());
                ps.setString(3, user.getSecondName());
                ps.setString(4, user.getEmail()); // Email
                ps.setString(5, user.getPassword()); // Password
                ps.setString(6, user.getPhone()); // Phone
                ps.setInt(7, user.getRole()); // role
                 ps.setString(8, user.getProfilePicture()); // Profile picture (nullable)

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLIntegrityConstraintViolationException e) {
                log.error("Username or email already exists, please try again: {}", user.getEmail());
                throw new IllegalStateException("Username or email already exists, please choose a different one.", e);
            }

        } catch (SQLException e) {
            log.error("SQL exception when registering user: {}", user.getUsername(), e);
        }
        return false;
    }

    private boolean validateUser(User user) {
        return user != null &&
                user.getUsername() != null && !user.getUsername().isBlank() && // Ensure 'username' is provided
                user.getPassword() != null && !user.getPassword().isBlank() && // Ensure 'password' is provided
                user.getEmail() != null && !user.getEmail().isBlank() && // Ensure 'email' is provided
                user.getPhone() != null && !user.getPhone().isBlank(); // Ensure 'phone' is provided
    }


    /**
     * Logss in the user by validating/checking the provided username and password with the database.
     *
     * @param username     the username of the user trying to log in
     * @param password the password of the user trying to log in
     * @return object if the login is successful, or null if the credentials are invalid
     * @throws IllegalArgumentException if either the username or password is null or blank
     */
    @Override
    public User login(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("You must enter a username to login");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("You must enter a password to login");
        }

        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String hashedPassword = rs.getString("password");

                     if (org.mindrot.jbcrypt.BCrypt.checkpw(password, hashedPassword)) {
                         int userRole = rs.getInt("role");
                         log.debug("Login successful for {} with Role={}", username, userRole);

                         return User.builder()
                                .id(rs.getInt("id"))
                                .username(rs.getString("username"))
                                .firstName(rs.getString("firstName"))
                                .secondName(rs.getString("secondName"))
                                .email(rs.getString("email"))
                                .password(rs.getString("password")) 
                                .phone(rs.getString("phone"))
                                .profilePicture(rs.getString("profile_picture"))
                                 .role(userRole)
                                 .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                                .build();
                    } else {
                        log.warn("Invalid login attempt for username: {}", username);
                        return null;
                    }
                }
            }

        } catch (SQLException e) {
            log.error("An error occurred when logging in user with username: {}", username, e);
        }
        return null;
    }

    private boolean isUsernameOrEmailTaken(String username, String email) {
        String checkSql = "SELECT COUNT(*) FROM Users WHERE username = ? OR email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, username);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            log.error("Error checking for duplicate username or email: {}", e.getMessage());
        }
        return false;
    }

    /**
     *Retrieves a user from the database using their user id.
     *this method queries the database to find a user associated with the given user id.
     *If found, it constructs and returns a user object with the associated data.
     * @param userId of the user to get.
     * @return User object if the user id exists in te database, otherwise returns null.
     */
    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return User.builder()
                            .id(rs.getInt("id"))
                            .username(rs.getString("username"))
                            .firstName(rs.getString("firstName"))
                            .secondName(rs.getString("secondName"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .phone(rs.getString("phone"))
                            .role(rs.getInt("role"))
                            .profilePicture(rs.getString("profile_picture"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build();
                }
            }
        } catch (SQLException e) {
            log.error("SQL error while retrieving user ID: " + userId, e);
        }
        return null;
    }

    /**
     *retrieves a user from the database using their email.
     *this method queries the database to find a user associated with the given email address.
     *If found, it constructs and returns a user object with the associated data.
     *
     * @param email address of the user to get.
     * @return User object if the email exists in the database, otherwise returns null.
     */
    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return User.builder()
                            .id(rs.getInt("id"))
                            .username(rs.getString("username"))
                            .firstName(rs.getString("firstName"))
                            .secondName(rs.getString("secondName"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .phone(rs.getString("phone"))
                            .profilePicture(rs.getString("profile_picture"))
                            .role(rs.getInt("role"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build();
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving user by email: {}", email, e);
        }
        return null;
    }

    /**
     *updates the role of a user in the database.
     *This method chamges the role of a user based on their user ID.
     *it makes sure that the role is updated correctly in the database.
     *
     * @param userId of the user whose role needs to be updated.
     * @param newRole to assign to the user (1 = Buyer, 2 = Seller, 3 = Admin).
     * @return true if the update was successful, false otherwise.
     */
    @Override
    public boolean updateUserRole(int userId, int newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newRole);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("SQL error while updating role for user ID: " + userId, e);
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = User.builder()
                        .id(rs.getInt("id"))
                        .username(rs.getString("username"))
                        .firstName(rs.getString("firstName"))
                        .secondName(rs.getString("secondName"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .phone(rs.getString("phone"))
                        .profilePicture(rs.getString("profile_picture"))
                        .role(rs.getInt("role"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
                users.add(user);
            }
        } catch (SQLException e) {
            log.error("Error retrieving all users", e);
        }
        return users;
    }

    @Override
    public void deleteUser(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}