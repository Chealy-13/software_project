package org.example.software_project.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.UserDao;
import org.example.software_project.Persistence.UserDaoImpl;
import org.example.software_project.business.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserDao userDao;

    public UserController() {
        this.userDao = new UserDaoImpl("database.properties");
    }


    @GetMapping("/registerPage")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("register")
    public String register(@RequestParam(name = "username") String username,
                           @RequestParam(name = "password") String password,
                           @RequestParam(name = "confirmPass") String confirm,
                           @RequestParam(name = "phone") String phone,
                           @RequestParam(name = "role", required = false, defaultValue = "1") int role, // Default to "buyer" if not provided
                           @RequestParam(name = "profile_picture", required = false) String profilePicture,
                           @RequestParam(name = "email") String email,
                           Model model, HttpSession session) {
        String errorMsg = null;

        if (username == null || username.isBlank()) {
            errorMsg = "Cannot register without a username";
        } else if (password == null || password.isBlank()) {
            errorMsg = "Cannot register without a password";
        } else if (!password.matches("^(?=.*[A-Z])(?=.*\\d).+$")) {
            errorMsg = "Password must contain at least one uppercase letter and one number.";
        } else if (confirm == null || confirm.isBlank() || !confirm.equals(password)) {
            errorMsg = "Passwords must match!";
        } else if (email == null || email.isBlank()) {
            errorMsg = "Cannot register without a valid email";
        } else if (phone == null || phone.isBlank()) {
            errorMsg = "Cannot register without a phone number";
        }

        if (errorMsg != null) {
            model.addAttribute("errorMessage", errorMsg);
            return "registration";
        }

        User newUser = User.builder()
                .name(username)
                .password(password)
                .email(email)
                .phone(phone)
                .role(role)
                .profile_picture(profilePicture)
                .build();

        try {
            boolean registered = userDao.register(newUser);
            if (registered) {
                session.setAttribute("currentUser", newUser);
                return "redirect:/payment";
            }
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", "Username or email already exists. Please choose a different one.");
            return "registration";
        }

        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "registration";
    }


    /**
     * This manages the login process by validating user credentials, authenticating the user, and
     * redirecting them to the home page when login is successful.
     *
     * @param //username The username provided by the user during the login stage.
     * @param// password The password provided by the user.
     * @param// model    The model object to pass notifications.
     * @param //session  The current HTTP session to store the authenticated user's details.
     *                 Redirects to "index" if successfully logged in or back to "login" if authentication fails.
     */

    @GetMapping("/loginPage")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("login")
    public String login(@RequestParam(name = "username") String username,
                        @RequestParam(name = "password") String password,
                        Model model, HttpSession session) {
        String errorMsg = null;
        if (username == null || username.isBlank()) {
            errorMsg = "Cannot login without a username";
        } else if (password == null || password.isBlank()) {
            errorMsg = "Cannot login without a password";
        }
        if (errorMsg != null) {
            model.addAttribute("errorMessage", errorMsg);
            return "login";
        }
        UserDao userDao = new UserDaoImpl("database.properties");
        User loggedInUser = userDao.login(username, password);
        if (loggedInUser != null) {
            String success = "Login successfully";
            model.addAttribute("message", success);
            session.setAttribute("currentUser", loggedInUser);
            return "index";
        } else {
            String failed = "Username/password incorrect.";
            model.addAttribute("errorMessage", failed);
            return "login";
        }
    }
}