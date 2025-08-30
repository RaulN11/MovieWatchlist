package project.movieslist.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import project.movieslist.model.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    List<Movie>findByGenreIgnoreCase(String genre);
    List<Movie>findByTitleIgnoreCase(String title);
    Optional<Movie> findFirstByTitleIgnoreCase(String title);
    void deleteByTitleIgnoreCase(String title);
}
