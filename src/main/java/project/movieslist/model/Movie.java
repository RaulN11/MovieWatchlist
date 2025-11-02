package project.movieslist.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Document(collection = "movies")
public class Movie{
    @Id
    private String id;
    private Integer tid;
    private String title;
    private String director;
    private String genre;
    private String posterPath;
    private String backdropPath;
    private Double popularity;
    private String overview;
    private int year;
    private String runtime;
    private int watchedCount;
    private int likedCount;
    private int ratingCount;
    private double ratingSum;
    private List<Review> reviews = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return id != null && id.equals(movie.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
