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

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    ClientService clientService;
    @Autowired
    MovieService movieService;
    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/addtowatched/{tid}")
    public Client addWatchedMovie(@PathVariable Integer tid, @RequestParam(required = false) Double rating,
                                  @RequestParam(required = false) String comment, Authentication authentication) {
        String username=authentication.getName();
        return clientService.addMovieToWatched(username,tid);
    }
    @DeleteMapping("/removefromwatched/{tid}")
    public Client removeWatchedMovie(@PathVariable Integer tid, Authentication auth) {
        String username=auth.getName();
        return clientService.removeMovieFromWatched(username,tid);
    }
    @PostMapping("/addtowatchlist/{tid}")
    public Client addToWatchlist(@PathVariable Integer tid, Authentication auth) {
        String username=auth.getName();
        return clientService.addMoviesToWatchlist(username, tid);
    }
    @DeleteMapping("/removefromwatchlist/{tid}")
    public Client removeFromWatchlist(@PathVariable Integer tid, Authentication auth) {
        String username=auth.getName();
        return clientService.removeMovieFromWatchlist(username, tid);
    }
    @PostMapping("/addtoliked/{tid}")
    public Client addToLikedMovie(@PathVariable Integer tid, Authentication auth) {
        String username = auth.getName();
        return clientService.addMovieToLiked(username, tid);
    }
    @DeleteMapping("/removefromliked/{tid}")
    public Client removeFromLikedMovie(@PathVariable Integer tid, Authentication auth) {
        String username=auth.getName();
        return clientService.removeMovieFromLiked(username, tid);
    }
    @PostMapping("/follow/{username}")
    public Client follow(@PathVariable String username, Authentication auth) {
        String username1 = auth.getName();
        return clientService.addToFollowing(username1,username);
    }
    @PostMapping("/addreview/{tid}")
    public Movie addReview(@RequestBody Review review, @PathVariable Integer tid, Authentication auth) {
        Optional<Movie> optMovie = movieService.getMovieByTid(tid);
        Movie movie = optMovie.orElseThrow(() -> new RuntimeException("Movie not found"));
        String username = auth.getName();
        Optional<Client> optClient = clientService.getUserByUsername(username);
        Client client = optClient.orElseThrow(() -> new RuntimeException("Client not found"));

        Review review1 = new Review();
        review1.setAuthor(client.getUsername());
        review1.setAuthorPicture(client.getProfilePicture());
        review1.setComment(review.getComment());
        review1.setRating(review.getRating());

        Map<String, Double> ratings = client.getMovieRatings();
        ratings.put(movie.getTitle(), review1.getRating());
        client.setMovieRatings(ratings);
        clientRepository.save(client);

        return movieService.addReviewToMovie(movie, review1);
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
