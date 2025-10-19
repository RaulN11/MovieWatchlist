package project.movieslist.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@ToString
@Document(collection = "users")
public class Client implements UserDetails {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private List<Movie> watchedMovies=new ArrayList<>();
    private List<Movie> watchList=new ArrayList<>();
    private List<Movie> likedMovies=new ArrayList<>();
    private List<String> following=new ArrayList<>();
    private List<String> followers=new ArrayList<>();
    private Map<String, Double>movieRatings=new HashMap<>();
    private Map<String, LocalDate>movieDates=new HashMap<>();
    private String verificationToken;
    private boolean isVerified;
    private String resetToken;
    private String profilePicture;
    private String bio;
    private String city;
    private String country;
    private String instagram;
    private LocalDate joinedDate;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }

    public enum UserRole {
        ROLE_ADMIN, ROLE_CLIENT
    }
    public Double getMovieRating(Movie movie){
        return movieRatings.getOrDefault(movie.getTitle(),0.0);
    }

}
