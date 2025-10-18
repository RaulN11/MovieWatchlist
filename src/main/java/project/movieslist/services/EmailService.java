package project.movieslist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = "http://localhost:8080/signup/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification - SeenIt");
        message.setText("Thank you for registering! Please click the link below to verify your email:\n\n"
                + verificationUrl
                + "\n\nIf you did not create this account, please ignore this email.");
        message.setFrom("noreply@seenit.com");

        mailSender.send(message);
    }
}