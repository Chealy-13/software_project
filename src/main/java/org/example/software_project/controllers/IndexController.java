package org.example.software_project.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class IndexController
{
    @GetMapping("/")
    public String getHome(HttpSession session, Model model) {
        String successMessage = (String) session.getAttribute("successMessage");

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        return "index";
    }
    @GetMapping("/index")
        public String showIndexPage(Model model) {
        return "index";
    }



    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "login";
    }

}
