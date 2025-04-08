package org.example.software_project.Persistence;

import org.example.software_project.business.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDao {

    boolean register (User user);
    User login(String username, String password);

    User getUserById(int userId);

    User getUserByEmail(String email);

    boolean updateUserRole(int userId, int newRole);

    List<User> getAllUsers();

    void deleteUser(Long userId);

    void updateUser(Long id, String username, String email, int role);

    boolean updateResetToken(int userId, String token);

    User getUserByResetToken(String token);

    boolean updatePassword(User user);}
