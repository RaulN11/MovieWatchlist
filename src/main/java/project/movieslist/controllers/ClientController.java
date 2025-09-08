package project.movieslist.controllers;

import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.services.ClientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    ClientService clientService;
    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/addtowatched")
    public Client addWatchedMovie(@RequestParam String title, @RequestParam(required = false) Double rating,
                                  @RequestParam(required = false) String comment, Authentication authentication) {
        String username=authentication.getName();
        return clientService.addMovieToWatchedListByTitle(username,title,rating,comment);
    }
    @PostMapping("/addtowatchlist/{title}")
    public Client addToWatchlist(@PathVariable String title, Authentication authentication) {
        String username=authentication.getName();
        return clientService.addMoviesToWatchlistByTitle(username,title);
    }
    @PostMapping("/addtoliked/{title}")
    public Client addToLikedMovie(@PathVariable String title, Authentication auth) {
        String username = auth.getName();
        return clientService.addMovieToLikedByTitle(username, title);
    }
    @PostMapping("/follow/{username}")
    public Client follow(@PathVariable String username, Authentication authentication) {
        String username1 = authentication.getName();
        return clientService.addToFollowing(username1,username);
    }
    @GetMapping("/picture")
    public Client addProfilePicture(@RequestBody String url, Authentication authentication) {
        String username1 = authentication.getName();
        return clientService.addProfilePicture(username1,url);
    }
}
