package project.movieslist.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Auditable;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
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
    private String verificationToken;
    private boolean isVerified;
    private String resetToken;
    private String profilePicture="https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_640.png";
    public enum UserRole {
        ROLE_ADMIN, ROLE_CLIENT
    }

}
