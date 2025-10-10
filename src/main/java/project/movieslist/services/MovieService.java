package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import project.movieslist.model.Movie;
import project.movieslist.model.Review;
import project.movieslist.repositories.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class MovieService {
    private final MovieRepository movieRepository;
    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
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
    public Page<Movie> getMoviesByGenre(Pageable pageable,String genre) {
        return movieRepository.findByGenreIgnoreCase(pageable,genre);
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
        return movieRepository.existsByTitleIgnoreCase(movie.getTitle());
    }
    public Optional<Movie> findFirstByTitle(String title) {
        return movieRepository.findFirstByTitleIgnoreCase(title);
    }
    public Page<Movie> getByTitleContaining(String title, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable);
    }
    public Movie addReviewToMovie(Movie movie, Review review) {
        if(movie.getReviews()==null){
            movie.setReviews(new ArrayList<Review>());
        }else {
            movie.getReviews().add(review);
        }
        return movieRepository.save(movie);
    }

}
