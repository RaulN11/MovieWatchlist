package project.movieslist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.movieslist.model.Movie;
import project.movieslist.services.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    MovieService movieService;
    @DeleteMapping("/delete/{title}")
    public void deleteMovie(@PathVariable String title) {
        movieService.deleteByTitle(title);
    }
    @PostMapping("/add")
    public void addMovies(@RequestBody List<Movie> movies) {
        movieService.addMovies(movies);
    }

}
