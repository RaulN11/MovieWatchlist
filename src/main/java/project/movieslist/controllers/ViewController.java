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
import project.movieslist.model.Movie;
import project.movieslist.services.ClientService;
import project.movieslist.services.MovieService;

@Controller
public class ViewController {
    @Autowired
    MovieService movieService;
    @Autowired
    ClientService clientService;
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/req/signup")
    public String signup() {
        return "signup";
    }
    @GetMapping("/allmovies")
    public String allMovies(@RequestParam(defaultValue="0") int page, Model model, HttpServletRequest request) {
        int pageSize=5;
        Pageable pageable= PageRequest.of(page,pageSize);
        var moviePage=movieService.getAllMovies(pageable);
        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", moviePage);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("prevPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", page < moviePage.getTotalPages() - 1 ? page + 1 : page);
        model.addAttribute("currentPath", request.getRequestURI());

        return "allmovies";
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
        return "allmovies";
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
        return "allmovies";
    }
    @GetMapping("/details/{title}")
    public String movieDetails(@PathVariable String title,Model model){
        var movies=movieService.getMoviesByTitle(title);
        Movie movie=movies.get(0);
        model.addAttribute("movie", movie);
        return "moviedetails";
    }
    @GetMapping("/watched")
    public String watchedMovies(@RequestParam(defaultValue="0") int page, Model model,HttpServletRequest request, Authentication auth) {
        int pageSize=5;
        Pageable pageable= PageRequest.of(page,pageSize);
        String username=auth.getName();
        var movies=clientService.getWatchedMoviesByUsername(username,pageable);
        model.addAttribute("movies", movies);
        model.addAttribute("currentPage", movies);
        model.addAttribute("totalPages", movies.getTotalPages());
        model.addAttribute("prevPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", page < movies.getTotalPages() ? page + 1 : page);
        model.addAttribute("currentPath", request.getRequestURI());
        return "allmovies";
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
        return "allmovies";
    }
    @GetMapping("/chat-test")
    public String chatTest() {
        return "chat"; // Returns chat-test.html via Thymeleaf
    }

}