package org.example.software_project.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.UserDao;
import org.example.software_project.Persistence.VehicleDao;
import org.example.software_project.business.User;
import org.example.software_project.business.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final UserDao userDao;
    private final VehicleDao vehicleDao;

    @Autowired
    public AdminController(UserDao userDao, VehicleDao vehicleDao) {
        this.userDao = userDao;
        this.vehicleDao = vehicleDao;
    }

    @PostMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam("userId") Long userId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null || currentUser.getId() == userId) {
            return "redirect:/admin?error=CannotDeleteSelf"; //make sure admin cant delete themselves
        }

        userDao.deleteUser(userId); //continue with delete if not admin
        return "redirect:/admin?success=UserDeleted";
    }

    @PostMapping("/admin/deleteListing")
    public String deleteListing(@RequestParam Long listingId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null || currentUser.getRole() != 3) {
            return "redirect:/loginPage";
        }

        vehicleDao.deleteVehicle(listingId);
        return "redirect:/admin";
    }

    @GetMapping("/admin/editUser")
    public String editUser(@RequestParam("userId") Long userId, Model model) {
        User user = userDao.getUserById(userId.intValue());
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/admin/updateUser")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam int role) {
        userDao.updateUser(id, username, email, role);
        return "redirect:/admin?success=UserUpdated";
    }

    @GetMapping("/admin/editListing")
    public String editListing(@RequestParam("listingId") Long listingId, Model model) {
        Vehicle listing = vehicleDao.getVehicleById(listingId);
        model.addAttribute("listing", listing);
        return "editListing";
    }

    @PostMapping("/admin/updateListing")
    public String updateListing(@RequestParam Long id,
                                @RequestParam String make,
                                @RequestParam String model,
                                @RequestParam double price) {
        vehicleDao.updateVehicle(id, make, model, price);
        return "redirect:/admin?success=ListingUpdated";
    }
}