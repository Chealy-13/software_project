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

    public FavoriteController(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }

    @PostMapping("/add")
    public String addFavorite(@RequestParam Long vehicleId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/loginPage";
        }

        favoriteDao.addFavorite((long)user.getId(), vehicleId);
        return "redirect:/favorites";
    }

    @PostMapping("/remove")
    public String removeFavorite(@RequestParam Long vehicleId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/loginPage";
        }

        favoriteDao.removeFavorite((long)user.getId(), vehicleId);
        return "redirect:/favorites";
    }

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
