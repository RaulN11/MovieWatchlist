package project.movieslist.controllers;

import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.services.ClientService;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    ClientService clientService;
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
}
