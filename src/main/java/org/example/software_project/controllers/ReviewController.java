package org.example.software_project.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final JdbcTemplate jdbcTemplate;

    public ReviewController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/{sellerId}")
    public String getReviews(@PathVariable Long sellerId, Model model) {
        List<Map<String, Object>> reviews = jdbcTemplate.queryForList(
                "SELECT * FROM Reviews WHERE seller_id = ?", sellerId);

        model.addAttribute("reviews", reviews);
        model.addAttribute("sellerId", sellerId);
        return "reviews";
    }


    @PostMapping("/add")
    public String addReview(@RequestParam int rating,
                            @RequestParam String comment,
                            @RequestParam Long sellerId,
                            @RequestParam Long userId) {

//        if (userId.equals(sellerId)) {
//            // redirect or show error message
//            return "redirect:/reviews?sellerId=" + sellerId;
//        }


        jdbcTemplate.update("INSERT INTO Reviews (rating, comment, seller_id, user_id) VALUES (?, ?, ?, ?)",
                rating, comment, sellerId, userId);

        return "redirect:/reviews/" + sellerId;
    }



    @PostMapping("/delete")
    public String deleteReview(@RequestParam Long reviewId,
                               @RequestParam Long sellerId,
                               @RequestParam Long userId) {

        Integer reviewOwnerId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM Reviews WHERE id = ?",
                new Object[]{reviewId},
                Integer.class
        );

        if (userId != null && userId.equals(Long.valueOf(reviewOwnerId))) {
            jdbcTemplate.update("DELETE FROM Reviews WHERE id = ?", reviewId);
        }

        return "redirect:/reviews";
    }

    @GetMapping
    public String showReviewPage(@RequestParam(required = false) Long sellerId, Model model) {
        // Get all sellers from the Users table where role is 'Seller'
        List<Map<String, Object>> sellers = jdbcTemplate.queryForList(
                "SELECT id, username AS name FROM Users WHERE role = 2");

        model.addAttribute("sellers", sellers);

        // Default to the first seller if none selected
        if (sellerId == null && !sellers.isEmpty()) {
            sellerId = ((Number) sellers.get(0).get("id")).longValue();
        }

        model.addAttribute("sellerId", sellerId);

        // Get reviews for the selected seller
        if (sellerId != null) {
            List<Map<String, Object>> reviews = jdbcTemplate.queryForList(
                    "SELECT * FROM Reviews WHERE seller_id = ?", sellerId);
            model.addAttribute("reviews", reviews);
        }

        return "reviews";
    }








}
