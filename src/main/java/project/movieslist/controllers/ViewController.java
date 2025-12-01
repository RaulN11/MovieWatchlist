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

import java.time.LocalDateTime;
import java.util.*;

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
        List<Movie> topUpcoming =tmDbService.getUpcomingMovies()
                .stream()
                .toList();
        var upcomingTitles = topUpcoming.stream()
                .map(Movie::getTitle)
                .toList();
        List<Movie> topTrending =tmDbService.getTrendingMovies()
                .stream()
                .filter(movie -> !upcomingTitles.contains(movie.getTitle()))
                .toList();
        model.addAttribute("trendingMovies", topTrending);
        model.addAttribute("upcomingMovies", topUpcoming);
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
            Optional<Client>clientOpt=clientService.getUserByUsername(username);
            Client client = clientOpt.get();
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
        int totalMinutes = 0;
        for (Movie movie : movies) {
            String runtimeStr = movie.getRuntime();
            if (runtimeStr == null || runtimeStr.equalsIgnoreCase("Unknown")) {
                continue;
            }
            try {
                int hours = 0;
                int minutes = 0;

                if (runtimeStr.contains("h")) {
                    String[] parts = runtimeStr.split("h");
                    hours = Integer.parseInt(parts[0].trim());

                    if (parts.length > 1 && parts[1].contains("m")) {
                        minutes = Integer.parseInt(parts[1].replace("m", "").trim());
                    }
                } else if (runtimeStr.contains("m")) {
                    minutes = Integer.parseInt(runtimeStr.replace("m", "").trim());
                }

                totalMinutes += (hours * 60 + minutes);
            } catch (Exception e) {

            }
        }
        int totalHours=totalMinutes/60;
        model.addAttribute("runtime", totalHours);
        model.addAttribute("movies", movies);
        model.addAttribute("client",client);
        return "watchlist";
    }
    @GetMapping("/profile/{id}")
    public String profile(Model model, Authentication auth, @PathVariable String id) {
        Optional<Client> clientOpt1=clientService.getUserById(id);
        Client client=clientOpt1.get();
        Map<Integer, LocalDateTime> dates=client.getMovieDates();
        List<Integer> recentTid=dates.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, LocalDateTime>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
        List<Movie> recentMovies=recentTid.stream()
                .map(tid->movieService.getMovieByTid(tid).orElse(null))
                .filter(movie -> movie != null)
                .toList();

        String username=auth.getName();
        Optional<Client> clientOpt=clientService.getUserByUsername(username);
        Client authenticatedClient =clientOpt.get();
        model.addAttribute("recentMovies",recentMovies);
        model.addAttribute("authenticatedClient", authenticatedClient);
        model.addAttribute("client", client);
        return "profile";
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
            case "directors" -> model.addAttribute("directors", tmDbService.fetchDirectorsByName(searched));
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
    @GetMapping("/chat")
    public String chat(Model model, Authentication auth){
        String username=auth.getName();
        Optional<Client> clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        model.addAttribute("client", client);
        List<String> followingNamesList=client.getFollowing();
        List<Client> followingList=new ArrayList<>();
        for(String name: followingNamesList) {
            followingList.add(clientService.getUserByUsername(name).get());
        }
        model.addAttribute("followingList", followingList);
        return "chat";
    }
    @GetMapping("/recommendations")
    public String recommendations(Model model, Authentication auth){
        String username=auth.getName();
        Client client=clientService.getUserByUsername(username).get();
        model.addAttribute("client", client);
        return "recommendations";
    }
}