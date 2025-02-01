package org.example.software_project.Persistence;

import lombok.extern.slf4j.Slf4j;
import org.example.software_project.business.User;

import java.sql.*;

@Slf4j
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

        String sql = "INSERT INTO Users (username,firstName, secondName, email, password, phone, profile_picture) VALUES(?, ?, ?, ?, ?,?,?)";
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
                 ps.setString(7, user.getProfilePicture()); // Profile picture (nullable)

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

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

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
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build();
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
                    return rs.getInt(1) > 0; // If the count is greater than 0, the username/email is taken
                }
            }
        } catch (SQLException e) {
            log.error("Error checking for duplicate username or email: {}", e.getMessage());
        }
        return false;
    }
}