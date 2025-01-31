package org.example.software_project.Persistence;

import org.example.software_project.business.User;

public interface UserDao {

    boolean register (User user);
    User login(String username, String password);

}
