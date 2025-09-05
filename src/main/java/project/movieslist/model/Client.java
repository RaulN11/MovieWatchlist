package project.movieslist.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@Document(collection = "users")
public class Client {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private LocalDate dateOfBirth;
    private UserRole role;
    private List<Movie> watchedMovies;
    private List<Movie> watchList;
    private List<Movie> likedMovies;
    private List<String> following;
    private List<String> followers;
    public enum UserRole {
        ROLE_ADMIN, ROLE_CLIENT
    }
}
