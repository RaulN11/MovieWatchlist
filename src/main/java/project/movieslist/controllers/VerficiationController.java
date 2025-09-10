package project.movieslist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.movieslist.model.Client;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.util.JWTUtil;

@RestController
public class VerficiationController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/req/signup/verify")
    public ResponseEntity verifyEmail(@RequestParam("token") String token) {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token Invalid or Expired");
        }
        String email;
        try {
            email = jwtUtil.getEmailFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
        }
        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (client.isVerified()) {
            return ResponseEntity.status(HttpStatus.OK).body("Already Verified");
        }

        if (client.getVerificationToken() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No verification pending");
        }

        if (!client.getVerificationToken().equals(token)) {
            System.out.println("client "+ client.getVerificationToken());
            System.out.println("token +"+ token);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token Mismatch");
        }
        client.setVerificationToken(null);
        client.setVerified(true);
        clientRepository.save(client);

        return ResponseEntity.status(HttpStatus.OK).body("Email Verified Successfully");
    }
}