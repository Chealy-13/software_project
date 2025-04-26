package org.example.software_project.controllers;
import org.example.software_project.business.Vehicle;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.FavoriteDao;
import org.example.software_project.business.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteDao favoriteDao;

    /**
     * Controller for handling operations related to user's favorite vehicles.
     * Allows users to add, remove, and view their favorite vehicles.
     */
    public FavoriteController(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }


    /**
     * Adds a vehicle to the current user's list of favorites.
     * If the user is not logged in, redirects to the login page.
     *
     * @param vehicleId the ID of the vehicle to add to favorites
     * @param session   the current HTTP session to retrieve the logged-in user
     * @return a redirect to the favorites page or login page
     */
    @PostMapping("/add")
    public String addFavorite(@RequestParam Long vehicleId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/loginPage";
        }

        if (!favoriteDao.isFavorite((long) user.getId(), vehicleId)) {
            favoriteDao.addFavorite((long) user.getId(), vehicleId);
        }
        return "redirect:/favorites";
    }

    /**
     * Removes a vehicle from the current user's list of favorites.
     * If the user is not logged in, redirects to the login page.
     *
     * @param vehicleId the ID of the vehicle to remove from favorites
     * @param session   the current HTTP session to retrieve the logged-in user
     * @return a redirect to the favorites page or login page
     */
    @PostMapping("/remove")
    public String removeFavorite(@RequestParam Long vehicleId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/loginPage";
        }

        favoriteDao.removeFavorite((long)user.getId(), vehicleId);
        return "redirect:/favorites";
    }


    /**
     * Displays the list of favorite vehicles for the current user.
     * If the user is not logged in, redirects to the login page.
     *
     * @param session the current HTTP session to retrieve the logged-in user
     * @param model   the Model to pass favorite vehicles to the view
     * @return the name of the favorites view page or a redirect to login page
     */
    @GetMapping
    public String viewFavorites(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/loginPage";
        }

        List<Vehicle> favoriteVehicles = favoriteDao.getFavoritesByUser((long)user.getId());
        for (Vehicle vehicle : favoriteVehicles) {
            if (vehicle.getImage_url() != null && !vehicle.getImage_url().isEmpty()) {
                vehicle.setImage_url(List.of(vehicle.getImage_url().get(0)));
            }
        }
        model.addAttribute("favorites", favoriteVehicles);
        return "favorites";
    }
}
