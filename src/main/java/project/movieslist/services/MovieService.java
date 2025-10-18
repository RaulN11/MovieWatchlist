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
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    public List<Movie> getMoviesByTitle(String title) {
        return movieRepository.findByTitleIgnoreCase(title);
    }
    public boolean movieExists(Movie movie) {
        return movieRepository.existsByTitleIgnoreCase(movie.getTitle());
    }
    public Optional<Movie> findFirstByTitle(String title) {
        return movieRepository.findFirstByTitleIgnoreCase(title);
    }
    public Movie addReviewToMovie(Movie movie, Review review) {
        if(movie.getReviews()==null){
            movie.setReviews(new ArrayList<Review>());
        }else {
            movie.getReviews().add(review);
        }
        movie.setRatingCount(movie.getRatingCount()+1);
        movie.setRatingSum(movie.getRatingSum()+review.getRating());
        return movieRepository.save(movie);
    }

}
