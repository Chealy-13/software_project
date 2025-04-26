package org.example.software_project.controllers;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs the ReviewController with a JdbcTemplate for database access.
     *
     * @param jdbcTemplate the JdbcTemplate used to execute SQL queries
     */
    public ReviewController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Retrieves all reviews for a given seller.
     *
     * @param sellerId the ID of the seller whose reviews are to be retrieved
     * @param model    the Model to pass attributes to the view
     * @return the name of the reviews view page
     */
    @GetMapping("/{sellerId}")
    public String getReviews(@PathVariable Long sellerId, Model model) {
        List<Map<String, Object>> reviews = jdbcTemplate.queryForList(
                "SELECT * FROM Reviews WHERE seller_id = ?", sellerId);

        model.addAttribute("reviews", reviews);
        model.addAttribute("sellerId", sellerId);
        return "reviews";
    }


    /**
     * Adds a new review for a specified seller.
     *
     * @param rating   the rating given by the user
     * @param comment  the comment provided by the user
     * @param sellerId the ID of the seller being reviewed
     * @param userId   the ID of the user submitting the review
     * @return a redirect to the seller's reviews page
     */
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



    /**
     * Deletes a review if the currently logged-in user is the owner of the review.
     *
     * @param reviewId the ID of the review to be deleted
     * @param sellerId the ID of the seller associated with the review
     * @param userId   the ID of the user attempting to delete the review
     * @return a redirect to the reviews page
     */
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


    /**
     * Displays the review page with a list of sellers and their reviews.
     *
     * @param sellerId optional seller ID to display reviews for a specific seller
     * @param model    the Model to pass attributes to the view
     * @return the name of the reviews view page
     */
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
