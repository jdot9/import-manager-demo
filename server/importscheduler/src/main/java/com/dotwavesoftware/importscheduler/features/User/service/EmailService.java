package com.dotwavesoftware.importscheduler.features.User.service;

import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.logging.Logger;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.AddressException;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException ex) {
            logger.warning("Invalid email address: " + email);
            return false;
        }
    }

    public void sendWelcomeEmail(String to, String firstName) {
        if (!isValidEmailAddress(to)) {
            logger.warning("Attempted to send welcome email to invalid address: " + to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("jasontestingemail9@gmail.com");
            message.setTo(to);
            message.setSubject("Import Manager ");
            message.setText("Dear " + firstName + ",\n\n" +
                          "Thank you for registering. \n\n" +
                          "Best regards,\n" +
                          "The Team");

            mailSender.send(message);
            logger.info("Welcome email sent successfully to " + to);
        } catch (Exception e) {
            logger.warning("Failed to send welcome email to " + to + ": " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        if (!isValidEmailAddress(to)) {
            logger.warning("Attempted to send password reset email to invalid address: " + to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("jasontestingemail9@gmail.com");
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("A password reset request has been made for your account. If you did not request this, please contact support at jasontestingemail9@gmail.com.");

            mailSender.send(message);
            logger.info("Password reset email sent successfully to " + to);
        } catch (Exception e) {
            logger.warning("Failed to send password reset email to " + to + ": " + e.getMessage());
        }
    }
} 