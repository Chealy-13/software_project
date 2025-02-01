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
            throw new IllegalArgumentException("You must enter a username, password, email, and phone number to proceed.");
        }

        String sql = "INSERT INTO Users (name, email, password, phone, profile_picture) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            if (conn == null) {
                log.error("Failed to get database connection.");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // Set the parameters for the SQL query
                ps.setString(1, user.getName()); // Name
                ps.setString(2, user.getEmail()); // Email
                ps.setString(3, user.getPassword()); // Password
                ps.setString(4, user.getPhone()); // Phone
                 ps.setString(5, user.getProfilePicture()); // Profile picture (nullable)

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLIntegrityConstraintViolationException e) {
                log.error("Username or email already exists, please try again: {}", user.getEmail());
                throw new IllegalStateException("Username or email already exists, please choose a different one.");
            }

        } catch (SQLException e) {
            log.error("SQL exception when registering user: {}", user.getName(), e);
        }
        return false;
    }

    private boolean validateUser(User user) {
        return user != null &&
                user.getName() != null && !user.getName().isBlank() && // Ensure 'name' (username) is provided
                user.getPassword() != null && !user.getPassword().isBlank() && // Ensure 'password' is provided
                user.getEmail() != null && !user.getEmail().isBlank() && // Ensure 'email' is provided
                user.getPhone() != null && !user.getPhone().isBlank(); // Ensure 'phone' is provided
    }


    /**
     * Logss in the user by validating/checking the provided username and password with the database.
     *
     * @param name     the username of the user trying to log in
     * @param password the password of the user trying to log in
     * @return object if the login is successful, or null if the credentials are invalid
     * @throws IllegalArgumentException if either the username or password is null or blank
     */
    @Override
    public User login(String name, String password) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("You must enter a username to login");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("You must enter a password to login");
        }

        String sql = "SELECT * FROM users WHERE name = ? AND password = ?"; // ✅ Removed COLLATE utf8mb4_bin
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return User.builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .phone(rs.getString("phone"))

                            .profilePicture(rs.getString("profile_picture"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .build();
                }
            }

        } catch (SQLException e) {
            log.error("An error occurred when logging in user with username: {}", name, e);
        }
        return null;
    }
}