package project.movieslist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.movieslist.model.Client;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.services.EmailService;
import project.movieslist.util.JWTUtil;
import java.time.LocalDate;

@RestController
public class RegistrationController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @PostMapping( "/signup")
    public ResponseEntity<String> createClient(@RequestBody Client client) {
        Client existingClient=clientRepository.findByEmail(client.getEmail());
        if(existingClient!=null) {
            if(existingClient.isVerified()){
                return new ResponseEntity<>("User already exists and verified", HttpStatus.BAD_REQUEST);
            }else{
                String verificationToken= JWTUtil.generateToken(existingClient.getEmail());
                existingClient.setVerificationToken(verificationToken);
                clientRepository.save(existingClient);
                return new ResponseEntity<>("Verification Email Resent", HttpStatus.OK);
            }
        }
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setJoinedDate(LocalDate.now());
        String verificationToken=JWTUtil.generateToken(client.getEmail());
        client.setVerificationToken(verificationToken);
        clientRepository.save(client);
        emailService.sendVerificationEmail(client.getEmail(),verificationToken);
        return new ResponseEntity<>("Registration Successful", HttpStatus.OK);
    }

}
