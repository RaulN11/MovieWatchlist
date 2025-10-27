package project.movieslist.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.model.Review;
import project.movieslist.services.ClientService;
import project.movieslist.services.MovieService;
import project.movieslist.services.TMDbService;

import java.util.ArrayList;
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

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/homepage")
    public String allMovies(Model model, HttpServletRequest request, Authentication auth) {
        if (auth != null) {
            String username=auth.getName();
            Optional<Client> clientOpt=clientService.getUserByUsername(username);
            Client client = clientOpt.get();
            model.addAttribute("client", client);
        }
        List<Movie> top6Upcoming =tmDbService.getUpcomingMovies()
                .stream()
                .limit(6)
                .toList();
        var upcomingTitles = top6Upcoming.stream()
                .map(Movie::getTitle)
                .toList();
        List<Movie> top6Trending =tmDbService.getTrendingMovies()
                .stream()
                .filter(movie -> !upcomingTitles.contains(movie.getTitle()))
                .limit(6)
                .toList();
        model.addAttribute("trendingMovies", top6Trending);
        model.addAttribute("upcomingMovies", top6Upcoming);
        model.addAttribute("currentPath", request.getRequestURI());
        return "homepage";

    }
    @GetMapping("/details/{tid}")
    public String movieDetails(@PathVariable Integer tid, Model model, Authentication auth){
        var moviesInDb=movieService.getMovieByTid(tid);
        Movie movie;
        movie = moviesInDb.orElseGet(() -> tmDbService.fetchMovieByTid(tid));
        if (auth != null) {
            String username=auth.getName();
            System.out.println("DEBUG: Authenticated user: " + username);
            Optional<Client>clientOpt=clientService.getUserByUsername(username);
            Client client = clientOpt.get();
            System.out.println("DEBUG: Client found: " + (client != null));
            List<Movie> likedMovies=client.getLikedMovies()!=null ? client.getLikedMovies(): List.of();
            List<Movie> watchedMovies=client.getWatchedMovies()!=null ? client.getWatchedMovies(): List.of();
            List<Movie> watchlist = client.getWatchList() != null ? client.getWatchList() : List.of();
            model.addAttribute("client",client);
            model.addAttribute("liked", likedMovies);
            model.addAttribute("watched", watchedMovies);
            model.addAttribute("watchlist", watchlist);
        }
        List<Review> reviews=movie.getReviews();
        model.addAttribute("movie", movie);
        model.addAttribute("reviews", reviews);
        return "moviedetails";
    }

    @GetMapping("/watched/{filter}")
    public String watchedMovies(Model model,HttpServletRequest request, Authentication auth, @PathVariable String filter){
        String username=auth.getName();
        Optional<Client>clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        var movies=clientService.getWatchedMoviesByUsername(username);
        switch (filter){
            case "all":
                model.addAttribute("movies", movies);
                break;
            case "liked":
                List<Movie> liked=new ArrayList<>();
                for(Movie movie:movies){
                    if(client.getLikedMovies().contains(movie)){
                        liked.add(movie);
                    }
                }
                model.addAttribute("movies", liked);
                break;
            case "highest":
                movies=movies.stream()
                        .sorted((m1,m2)->{
                            Double rating1=client.getMovieRating(m1);
                            Double rating2=client.getMovieRating(m2);
                            return Double.compare(rating2,rating1);
                        })
                        .toList();
                model.addAttribute("movies", movies);
                break;
            case "lowest":
                movies=movies.stream()
                        .sorted((m1,m2)->{
                            Double rating1=client.getMovieRating(m1);
                            Double rating2=client.getMovieRating(m2);
                            return Double.compare(rating1,rating2);
                        })
                        .toList();
                model.addAttribute("movies", movies);
                break;
        }
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
    @GetMapping("/yourprofile")
    public String profile(Model model, Authentication auth) {
        String username=auth.getName();
        Optional<Client> clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        model.addAttribute("client",client);
        return "yourprofile";
    }
    @GetMapping("/searchMenu/{type}/{searched}")
    public String searchMenu(@PathVariable String searched,@PathVariable String type, Model model, Authentication auth){
        List<Movie>byTitle= tmDbService.fetchMoviesByTitle(searched);
        if(auth!=null) {
            String username = auth.getName();
            Optional<Client> clientOpt = clientService.getUserByUsername(username);
            Client client = clientOpt.get();
            model.addAttribute("client", client);
        }
        switch (type.toLowerCase()){
            case "movies" -> model.addAttribute("movies", tmDbService.fetchMoviesByTitle(searched));
            case "actors" -> model.addAttribute("actors", tmDbService.fetchActorsByName(searched));
        }
        model.addAttribute("searched", searched);

        return "searchmenu";
    }
    @GetMapping("/moviesbyactor/{actorName}")
    public String showMoviesByActor(@PathVariable String actorName, Authentication authentication,Model model){
        if(authentication!=null){
            String username=authentication.getName();
            Optional<Client>clientOpt=clientService.getUserByUsername(username);
            Client client=clientOpt.get();
            model.addAttribute("client",client);
        }
        List<Movie> movies=tmDbService.fetchMovieByActorName(actorName);
        model.addAttribute("movies", movies);
        return "moviesbyactor";

    }


}