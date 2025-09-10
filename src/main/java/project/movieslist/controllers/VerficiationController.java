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
        String email = jwtUtil.getEmailFromToken(token);
        Client client = clientRepository.findByEmail(email);
        if(client==null || client.getVerificationToken()==null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token Expired 1");
        }
        if(!jwtUtil.validateToken(token) || !client.getVerificationToken().equals(token)) {
            System.out.println("parameter token"+token);
            System.out.println("user token"+client.getVerificationToken());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token Expired 2");
        }
        client.setVerificationToken(null);
        client.setVerified(true);
        clientRepository.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body("Token Verified");
    }
}
