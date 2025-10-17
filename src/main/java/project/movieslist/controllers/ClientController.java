package project.movieslist.controllers;

import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.model.Review;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.services.ClientService;
import project.movieslist.services.MovieService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    ClientService clientService;
    @Autowired
    MovieService movieService;

    @PostMapping("/addtowatched")
    public Client addWatchedMovie(@RequestParam String title, @RequestParam(required = false) Double rating,
                                  @RequestParam(required = false) String comment, Authentication authentication) {
        String username=authentication.getName();
        return clientService.addMovieToWatchedListByTitle(username,title,rating,comment);
    }
    @DeleteMapping("/removefromwatched/{title}")
    public Client removeWatchedMovie(@PathVariable String title, Authentication auth) {
        String username=auth.getName();
        return clientService.removeMovieFromWatchedListByTitle(username,title);
    }
    @PostMapping("/addtowatchlist/{title}")
    public Client addToWatchlist(@PathVariable String title, Authentication auth) {
        String username=auth.getName();
        return clientService.addMoviesToWatchlistByTitle(username,title);
    }
    @DeleteMapping("/removefromwatchlist/{title}")
    public Client removeFromWatchlist(@PathVariable String title, Authentication auth) {
        String username=auth.getName();
        return clientService.removeMovieFromWatchlistListByTitle(username,title);
    }
    @PostMapping("/addtoliked/{title}")
    public Client addToLikedMovie(@PathVariable String title, Authentication auth) {
        String username = auth.getName();
        return clientService.addMovieToLikedByTitle(username, title);
    }
    @DeleteMapping("/removefromliked/{title}")
    public Client removeFromLikedMovie(@PathVariable String title, Authentication auth) {
        String username=auth.getName();
        return clientService.removeMovieFromLikedListByTitle(username, title);
    }
    @PostMapping("/follow/{username}")
    public Client follow(@PathVariable String username, Authentication auth) {
        String username1 = auth.getName();
        return clientService.addToFollowing(username1,username);
    }
    @PostMapping("/addreview/{title}")
    public Movie addReview(@RequestBody Review review,@PathVariable String title, Authentication auth) {
        Optional<Movie> optMovie=movieService.findFirstByTitle(title);
        Movie movie=optMovie.orElse(null);
        String username=auth.getName();
        Optional<Client> optClient=clientService.getUserByUsername(username);
        Client client=optClient.orElse(null);
        Review review1=new Review();
        review1.setAuthor(client.getUsername());
        review1.setAuthorPicture(client.getProfilePicture());
        review1.setComment(review.getComment());
        review1.setRating(review.getRating());
        return movieService.addReviewToMovie(movie,review1);
    }
    @PostMapping("/addpicture")
    public Client addProfilePicture(@RequestBody String url, Authentication auth) {
        String username = auth.getName();
        Optional<Client> optClient= clientService.getUserByUsername(username);
        Client client = optClient.get();
        return clientService.addProfilePicture(client,url);
    }
    @PostMapping("/addbio")
    public Client addBio(@RequestBody String bio, Authentication auth) {
        String username=auth.getName();
        Optional<Client> optClient=clientService.getUserByUsername(username);
        Client client=optClient.get();
        return clientService.addBio(client,bio);

    }
    @PostMapping("/addcity")
    public Client addCity(@RequestBody String city, Authentication auth) {
        String username=auth.getName();
        Optional<Client> optClient=clientService.getUserByUsername(username);
        Client client=optClient.get();
        return clientService.addCity(client,city);
    }
    @PostMapping("/addcountry")
    public Client addCountry(@RequestBody String country, Authentication auth) {
        String username=auth.getName();
        Optional<Client> optClient=clientService.getUserByUsername(username);
        Client client=optClient.get();
        return clientService.addCountry(client,country);
    }
}
