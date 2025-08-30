package project.movieslist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.movieslist.model.Movie;
import project.movieslist.services.MovieService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;
    @GetMapping("/all")
    public List<Movie> getAll() {
       return movieService.getAllMovies();
    }
    @GetMapping("/genre/{genre}")
    public List<Movie> getByGenre(@PathVariable String genre) {
        return movieService.getMoviesByGenre(genre);
    }
    @GetMapping("/title/{title}")
    public List<Movie> getByTitle(@PathVariable String title) {
        return movieService.getMoviesByTitle(title);
    }


}
