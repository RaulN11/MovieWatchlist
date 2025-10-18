package project.movieslist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.movieslist.model.Client;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.util.JWTUtil;

@Controller  // Changed from @RestController to @Controller
public class VerificationController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/signup/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        if (!jwtUtil.validateToken(token)) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "The verification link is invalid or has expired.");
            return "verification";
        }
        String email;
        try {
            email = jwtUtil.getEmailFromToken(token);
        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Invalid verification token.");
            return "verification";
        }

        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "User not found. Please sign up again.");
            return "verification";
        }
        if (client.isVerified()) {
            model.addAttribute("success", true);
            return "verification";
        }
        if (client.getVerificationToken() == null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "No verification pending for this account.");
            return "verification";
        }

        if (!client.getVerificationToken().equals(token)) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Verification token mismatch. Please try again.");
            return "verification";
        }
        client.setVerificationToken(null);
        client.setVerified(true);
        clientRepository.save(client);

        model.addAttribute("success", true);
        return "verification";
    }
}