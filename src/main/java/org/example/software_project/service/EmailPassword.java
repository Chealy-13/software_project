package org.example.software_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailPassword {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Car Marketplace - Reset Your Password");
        message.setText("Click the following link to reset your password:\n\n" + resetLink +
                "\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);
    }

   public void sendConfirmationEmail(String toEmail, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to Car Marketplace!");
        message.setText("Hi " + firstName + ",\n\nYour account has been created successfully.\n\n"
                + "Enjoy using our service!\n\nCar Marketplace Team");
        mailSender.send(message);
    }
}

