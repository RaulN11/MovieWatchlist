package project.movieslist.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Director {
    private Integer id;
    private String fullName;
    private String picture;
    private String birthDate;
    private String birthPlace;
    private Double popularity;

}
