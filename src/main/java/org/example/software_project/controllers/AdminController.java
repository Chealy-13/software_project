package org.example.software_project.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.FavoriteDao;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Controller
public class AdminController {

    private final UserDao userDao;
    private final VehicleDao vehicleDao;
    private final FavoriteDao favoriteDao;

    @Autowired
    public AdminController(UserDao userDao, VehicleDao vehicleDao, FavoriteDao favoriteDao) {
        this.userDao = userDao;
        this.vehicleDao = vehicleDao;
        this.favoriteDao = favoriteDao;
    }

    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null || currentUser.getRole() != 3) {
            return "redirect:/loginPage";
        }

        List<User> users = userDao.getAllUsers();
        List<Vehicle> listings = vehicleDao.getAllVehicles();
        List<Vehicle> flaggedListings = vehicleDao.getFlaggedVehicles();
        List<Vehicle> topFavoritedVehicles = favoriteDao.getTopFavoritedVehiclesWithCounts();

//        debug for ads not showing in admindash
//        System.out.println("Flagged listings found: " + flaggedListings.size());

        //calc stats
        long totalUsers = users.size();
        long totalListings = listings.size();
        long totalFlagged = flaggedListings.size();


        //calc new users this week
        long newUsersThisWeek = users.stream()
                .filter(user -> {
                    if (user.getCreatedAt() != null) {
                        LocalDate createdDate = user.getCreatedAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return createdDate.isAfter(LocalDate.now().minusDays(7));
                    }
                    return false;
                })
                .count();

        //create list of new users and thier details
        List<User> newUsers = users.stream()
                .filter(user -> user.getCreatedAt() != null &&
                        user.getCreatedAt().atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .isAfter(LocalDate.now().minusDays(7))
                )
                .toList();


        //adding attributes to model
        model.addAttribute("users", users);
        model.addAttribute("listings", listings);
        model.addAttribute("flaggedListings", flaggedListings);
        model.addAttribute("topFavoritedVehicles", topFavoritedVehicles);
        //adding counts/stats to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalListings", totalListings);
        model.addAttribute("totalFlagged", totalFlagged);
        model.addAttribute("newUsersThisWeek", newUsersThisWeek);
        model.addAttribute("newUsers", newUsers);


        return "adminDashboard";
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
    @PostMapping("/admin/flagListing")
    public String flagListing(@RequestParam("listingId") Long listingId,
                              HttpServletRequest request) {
        vehicleDao.setFlagStatus(listingId, true);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/admin/unflagListing")
    public String unflagListing(@RequestParam("listingId") Long listingId,
                                HttpServletRequest request) {
        vehicleDao.setFlagStatus(listingId, false);
        return "redirect:" + request.getHeader("Referer");
    }


}