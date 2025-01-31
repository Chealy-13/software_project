package org.example.software_project.controllers;

import org.springframework.web.bind.annotation.GetMapping;

public class IndexController
{

    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "login";
    }

}
