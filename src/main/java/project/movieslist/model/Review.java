package project.movieslist.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review {
    private String author;
    private Double rating;
    private String comment;
}
