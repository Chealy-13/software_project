package org.example.software_project.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.UserDao;
import org.example.software_project.Persistence.VehicleDao;
import org.example.software_project.business.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

}