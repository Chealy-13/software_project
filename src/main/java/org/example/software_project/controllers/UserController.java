package org.example.software_project.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.UserDao;
import org.example.software_project.Persistence.UserDaoImpl;
import org.example.software_project.Persistence.VehicleDao;
import org.example.software_project.Persistence.VehicleDaoImpl;
import org.example.software_project.business.User;
import org.example.software_project.business.Vehicle;
import org.example.software_project.service.EmailPassword;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserDao userDao;
    private final VehicleDao vehicleDao;
    @Autowired
    private EmailPassword emailPassword;


    @Autowired
    public UserController(UserDao userDao, VehicleDao vehicleDao) {
        this.userDao = userDao;
        this.vehicleDao = vehicleDao;
    }


    @GetMapping("/registerPage")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("register")
    public String register(@RequestParam(name = "username") String username,
                           @RequestParam(name = "firstName") String firstName,
                           @RequestParam(name = "secondName") String secondName,
                           @RequestParam(name = "password") String password,
                           @RequestParam(name = "confirmPass") String confirm,
                           @RequestParam(name = "phone") String phone,
                           @RequestParam(name = "profile_picture", required = false) String profilePicture,
                           @RequestParam(name = "email") String email,
                           @RequestParam(name = "role") int role,
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
                .username(username)
                .password(password)
                .firstName(firstName)
                .secondName(secondName)
                .email(email)
                .phone(phone)
                .profilePicture(profilePicture)
                .role(role)
                .build();

        try {
            boolean registered = userDao.register(newUser);
            if (registered) {
                //get saved user from db to get the right id
                User savedUser = userDao.getUserByEmail(email);
                if (savedUser != null) {
                    session.setAttribute("currentUser", savedUser);

                    //log the user id to make sure its correct
                    log.debug("User stored in session after registration: ID={} Username={}", savedUser.getId(), savedUser.getUsername());

                    return "redirect:/";
                } else {
                    log.error("User registered but not found in DB!");
                    model.addAttribute("errorMessage", "User registered but not found.");
                    return "registration";
                }
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
     * @param //session  The current HTTP session to store the authenticated user's details.
     *                   Redirects to "index" if successfully logged in or back to "login" if authentication fails.
     * @param// password The password provided by the user.
     * @param// model    The model object to pass notifications.
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
            errorMsg = "Cannot login without a name";
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
            session.setAttribute("currentUser", loggedInUser);

            //Log correct user ID
            log.debug("User stored in session after login: ID={} Username={}", loggedInUser.getId(), loggedInUser.getUsername());

            return "redirect:/profile";
        } else {
            model.addAttribute("errorMessage", "Name/password incorrect.");
            return "login";
        }
    }

    /**
     * Showcases the logged-in user's profile details.
     * Only logged-in user can view the profile information.
     *
     * @param model   The model object to pass user details to the profile view.
     * @param session The current HTTP session to get the logged-in user's information.
     *                Returns the "profile" view if the user is logged in, otherwise redirects to the login page.
     */
    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("currentUser");
        if (loggedInUser == null || loggedInUser.getId() <= 0) {
            model.addAttribute("errorMessage", "You need to log in to view your profile.");
            return "redirect:/loginPage";
        }

        log.debug("Profile page accessed by User ID={} Username={}", loggedInUser.getId(), loggedInUser.getUsername());

        model.addAttribute("user", loggedInUser);
        return "profile";
    }
    @GetMapping("/editProfile")
    public String showEditProfileForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/loginPage";
        }

        model.addAttribute("user", currentUser);
        return "editProfile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam("id") int id,
                                @RequestParam("username") String username,
                                @RequestParam("firstName") String firstName,
                                @RequestParam("secondName") String secondName,
                                @RequestParam("email") String email,
                                @RequestParam("phone") String phone,
                                HttpSession session,
                                Model model) {

        User updatedUser = User.builder()
                .id(id)
                .username(username)
                .firstName(firstName)
                .secondName(secondName)
                .email(email)
                .phone(phone)
                .build();

        boolean success = userDao.updateUserProfile(updatedUser);

        if (success) {
            session.setAttribute("currentUser", updatedUser);
            return "redirect:/profile";
        } else {
            model.addAttribute("errorMessage", "Failed to update profile.");
            return "editProfile";
        }
    }


    /**
     * This logs out from the current user invalidating their session.
     *
     * @param model The model object to display a logout message.
     *              Returns to the index.html page after successfully logging out the user.
     */
    @GetMapping("/logout")
    public String logout(Model model, HttpSession session) {
        session.setAttribute("currentUser", null);

        model.addAttribute("message", "Logout successful!");
        return "index";
    }

    /**
     * Handles the switching of user roles between Buyer and Seller.
     * checks that the user exists in the database before updating their role
     * and makes the change in both the database and the current session.
     *
     * @param userId  of the user whose role is being changed.
     * @param newRole to be assigned (1 for Buyer, 2 for Seller).
     * @param session the current HTTP session to update the user role.
     * @return A redirect to the profile page with a success or error message.
     */
    @PostMapping("/switchRole")
    public String switchUserRole(@RequestParam("userId") int userId,
                                 @RequestParam("newRole") int newRole,
                                 HttpSession session) {
        log.debug("switchRole triggered for User ID: {} with newRole: {}", userId, newRole);

        if (userId <= 0) {
            log.error("Invalid user ID received!");
            return "redirect:/profile?error=InvalidUser";
        }

        User user = userDao.getUserById(userId);

        if (user == null) {
            log.error("User not found in database!");
            return "redirect:/profile?error=UserNotFound";
        }

        //update the user role
        user.setRole(newRole);
        boolean success = userDao.updateUserRole(user.getId(), newRole);

        if (success) {
            session.setAttribute("currentUser", user);
            log.debug("User role updated successfully to {}", newRole);
            return "redirect:/profile?success=RoleUpdated";
        } else {
            log.error("Failed to update user role!");
            return "redirect:/profile?error=UpdateFailed";
        }
    }


    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {
        return "forgotPassword";
    }

    @PostMapping("/sendResetLink")
    public String sendResetLink(@RequestParam("email") String email, Model model) {
        User user = userDao.getUserByEmail(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(1));
            userDao.updateResetToken(user.getId(), token);

            String resetLink = "http://localhost:8080/resetPassword?token=" + token;

            emailPassword.sendPasswordResetEmail(user.getEmail(), resetLink);
            model.addAttribute("message", "Reset link sent to your email.");
        } else {
            model.addAttribute("errorMessage", "No account found with that email.");
        }

        return "login";
    }

    @GetMapping("/resetPassword")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "resetPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                Model model) {
        User user = userDao.getUserByResetToken(token);

        if (user != null && user.getTokenExpiry().isAfter(LocalDateTime.now())) {
            user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            user.setResetToken(null);
            user.setTokenExpiry(null);

            userDao.updatePassword(user);

            model.addAttribute("message", "Password reset successfully!");
            return "login";
        } else {
            model.addAttribute("errorMessage", "Invalid or expired token.");
            return "resetPassword";
        }
    }
}


