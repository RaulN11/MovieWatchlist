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
                .limit(6)
                .toList();
        List<Movie> top6Upcoming=tmDbService.getUpcomingMovies()
                .stream()
                .limit(6)
                .toList();
        Optional<Client> client=clientService.getUserByUsername(username);
        model.addAttribute("client",client.get());
        model.addAttribute("trendingMovies", top6Trending);
        model.addAttribute("upcomingMovies", top6Upcoming);
        model.addAttribute("currentPath", request.getRequestURI());
        return "homepage";

    }
    @GetMapping("/genre/{genre}")
    public String allMoviesByGenre(@PathVariable String genre,@RequestParam(defaultValue="0") int page, Model model,HttpServletRequest request) {
        int pageSize=5;
        Pageable pageable= PageRequest.of(page,pageSize);
        var moviePage=movieService.getMoviesByGenre(pageable,genre);
        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", moviePage);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("prevPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", page < moviePage.getTotalPages() - 1 ? page + 1 : page);
        model.addAttribute("currentPath", request.getRequestURI());
        return "homepage";
    }

    @GetMapping("/title/{title}")
    public String allMoviesByTitle(@PathVariable String title,@RequestParam(defaultValue="0") int page, Model model,HttpServletRequest request) {
        int pageSize=5;
        Pageable pageable= PageRequest.of(page,pageSize);
        var moviePage=movieService.getByTitleContaining(title,pageable);
        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", moviePage);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("prevPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", page < moviePage.getTotalPages() - 1 ? page + 1 : page);
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
    public String watchedMovies(@RequestParam(defaultValue="0") int page, Model model,HttpServletRequest request, Authentication auth) {
        int pageSize=20;
        Pageable pageable= PageRequest.of(page,pageSize);
        String username=auth.getName();
        Optional<Client>clientOpt=clientService.getUserByUsername(username);
        Client client=clientOpt.get();
        var movies=clientService.getWatchedMoviesByUsername(username,pageable);
        model.addAttribute("movies", movies);
        model.addAttribute("client",client);
        model.addAttribute("currentPage", movies);
        model.addAttribute("totalPages", movies.getTotalPages());
        model.addAttribute("prevPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", page < movies.getTotalPages() ? page + 1 : page);
        model.addAttribute("currentPath", request.getRequestURI());
        return "diary";
    }

    @GetMapping("/watchlist")
    public String watchlistMovies(@RequestParam(defaultValue = "0")int page, Model model, HttpServletRequest request, Authentication auth) {
        int pageSize=5;
        Pageable pageable= PageRequest.of(page,pageSize);
        String username=auth.getName();
        var movies=clientService.getWatchlistByUsername(username,pageable);
        model.addAttribute("movies", movies);
        model.addAttribute("currentPage", movies);
        model.addAttribute("totalPages", movies.getTotalPages());
        model.addAttribute("prevPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", page < movies.getTotalPages() ? page + 1 : page);
        model.addAttribute("currentPath", request.getRequestURI());
        return "homepage";
    }
    @GetMapping("/profile")
    public String profile(Model model) {
        return "profile";
    }

}