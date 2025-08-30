package project.movieslist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.movieslist.model.Movie;
import project.movieslist.repositories.MovieRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    public Optional<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
    }
    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenreIgnoreCase(genre);
    }
    public long getMovieCount(){
        return movieRepository.count();
    }
    public List<Movie> getMoviesByTitle(String title) {
        return movieRepository.findByTitleIgnoreCase(title);
    }
    public void deleteByTitle(String title) {
        movieRepository.deleteByTitleIgnoreCase(title);
    }
    public void addMovies(List<Movie> movies) {
        movieRepository.saveAll(movies);
    }
    public boolean movieExists(Movie movie) {
        return movieRepository.existsById(movie.getId());
    }
    public Optional<Movie> findFirstByTitle(String title) {
        return movieRepository.findFirstByTitleIgnoreCase(title);
    }

}
