package project.movieslist.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Auditable;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    private LocalDate dateOfBirth;
    private UserRole role;
    private List<Movie> watchedMovies=new ArrayList<>();
    private List<Movie> watchList=new ArrayList<>();
    private List<Movie> likedMovies=new ArrayList<>();
    private List<String> following=new ArrayList<>();
    private List<String> followers=new ArrayList<>();
    private String verificationToken;
    private boolean isVerified;
    private String resetToken;
    private String profilePicture="https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_640.png";

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

}
