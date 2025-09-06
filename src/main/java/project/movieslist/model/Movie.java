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
@EqualsAndHashCode
@Document(collection = "movies")
public class Movie {
    @Id
    private String id;
    private String title;
    private String director;
    private String genre;
    private String posterPath;
    private int year;
    private Double rating;
    private String comment;
    private List<Review> reviews = new ArrayList<>();

}
