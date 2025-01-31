package org.example.software_project.Persistence;

import lombok.extern.slf4j.Slf4j;
import org.example.software_project.business.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
@Slf4j
public class UserDaoImpl extends MySQLDao implements UserDao {

    public UserDaoImpl(){
        super();
    }

    public UserDaoImpl(Connection conn){
        super(conn);
    }

    public UserDaoImpl(String propertiesFilename){
        super(propertiesFilename);
    }


    @Override
    public boolean register(User user) {
        if (!validateUser(user)) {
            throw new IllegalArgumentException("You must enter a username, password, email, and phone number to proceed.");
        }

        String sql = "INSERT INTO Users (name, email, password, phone, role, profile_picture) VALUES(?, ?, ?, ?, ?, ?)";
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
                ps.setInt(5, user.getRole()); // Role (default is 1, buyer)
                ps.setString(6, user.getProfile_picture()); // Profile picture (nullable)

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


}
