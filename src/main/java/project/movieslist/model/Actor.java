package project.movieslist.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@Document(collection = "actors")
public class Actor {
    private Integer id;
    private String fullName;
    private String picture;
}
