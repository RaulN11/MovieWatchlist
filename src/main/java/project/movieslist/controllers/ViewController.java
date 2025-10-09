package project.movieslist.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.model.Review;
import project.movieslist.services.ClientService;
import project.movieslist.services.MovieService;
import project.movieslist.services.TMDbService;

import java.util.List;
import java.util.Optional;

@Controller
public class ViewController {
    @Autowired
    MovieService movieService;
    @Autowired
    ClientService clientService;
    @Autowired
    TMDbService tmDbService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/req/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/homepage")
    public String allMovies(Model model, Authentication auth,HttpServletRequest request) {
        String username=auth.getName();
        List<Movie> top6Trending=tmDbService.getTrendingMovies()
                .stream()
                .limit(5)
                .toList();
        List<Movie> top6Upcoming=tmDbService.getUpcomingMovies()
                .stream()
                .limit(5)
                .toList();
        Optional<Client> client=clientService.getUserByUsername(username);
        model.addAttribute("client",client.get());
        model.addAttribute("trendingMovies", top6Trending);
        model.addAttribute("upcomingMovies", top6Upcoming);
        model.addAttribute("currentPath", request.getRequestURI());
        return "homepage";

    }
    @GetMapping("/details/{title}")
    public String movieDetails(@PathVariable String title, Model model, Authentication auth){
        var moviesInDb=movieService.getMoviesByTitle(title);
        Movie movie;
        if(!moviesInDb.isEmpty()){
            movie=moviesInDb.get(0);
        }else{
            movie= tmDbService.fetchMovieByTitle(title);
            if(movie==null){
                throw new RuntimeException("Movie not found");
            }
        }
        List<Review> reviews=movie.getReviews();
        String username=auth.getName();
        Optional<Client> clientOpt = clientService.getUserByUsername(username);
        Client client = clientOpt.orElseThrow(() -> new RuntimeException("Client not found"));
        List<Movie> likedMovies = client.getLikedMovies() != null ? client.getLikedMovies() : List.of();
        List<Movie> watchedMovies = client.getWatchedMovies() != null ? client.getWatchedMovies() : List.of();
        List<Movie> watchlist = client.getWatchList() != null ? client.getWatchList() : List.of();
        model.addAttribute("movie", movie);
        model.addAttribute("client",client);
        model.addAttribute("liked", likedMovies);
        model.addAttribute("watched", watchedMovies);
        model.addAttribute("watchlist", watchlist);
        model.addAttribute("reviews", reviews);
        return "moviedetails";
    }

    @GetMapping("/watched")
    public String watchedMovies(Model model,HttpServletRequest request, Authentication auth) {
        String username=auth.getName();
        Optional<Client>clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        var movies=clientService.getWatchedMoviesByUsername(username);
        model.addAttribute("movies", movies);
        model.addAttribute("client",client);
        return "diary";
    }

    @GetMapping("/watchlist")
    public String watchlistMovies(Model model, HttpServletRequest request, Authentication auth) {
        String username=auth.getName();
        Optional<Client>clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        var movies=clientService.getWatchlistByUsername(username);
        model.addAttribute("movies", movies);
        model.addAttribute("client",client);
        return "diary";
    }

    @GetMapping("/liked")
    public String likedMovies(Model model, HttpServletRequest request, Authentication auth) {
        String username=auth.getName();
        Optional<Client>clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        var movies=clientService.getLikedByUsername(username);
        model.addAttribute("movies", movies);
        model.addAttribute("client",client);
        return "diary";
    }
    @GetMapping("/profile")
    public String profile(Model model) {
        return "profile";
    }
    @GetMapping("/searchMenu/{type}/{searched}")
    public String searchMenu(@PathVariable String searched,@PathVariable String type, Model model, Authentication auth){
        List<Movie>byTitle= tmDbService.fetchMoviesByTitle(searched);
        String username=auth.getName();
        Optional<Client>clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        model.addAttribute("client",client);
        switch (type.toLowerCase()){
            case "movies" -> model.addAttribute("movies", tmDbService.fetchMoviesByTitle(searched));
            case "actors" -> model.addAttribute("actors", tmDbService.fetchActorsByName(searched));
        }
        model.addAttribute("searched", searched);

        return "searchmenu";
    }

}