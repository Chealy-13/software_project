package org.example.software_project.controllers;

import org.example.software_project.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendMessage")
    public String
    sendMessage(@RequestParam("vehicleId") int vehicleId,
                @RequestParam("buyerEmail")String buyerEmail,
                @RequestParam("message") String message){
        String subject = "New message about vehicle #" + vehicleId;
        String body = "Buyer's email: " + buyerEmail + "\n\nMessage: " + message;

        emailService.sendEmail("carmarketplaceseller@gmail.com", subject, body);

        return "allVehicles";
    }

    @GetMapping("/allVehicles")
    public String allVehicles(@RequestParam(value = "messageSent", required = false) String messageSent, Model model) {
        model.addAttribute("messageSent", messageSent);
        return "allVehicles";
    }




}
