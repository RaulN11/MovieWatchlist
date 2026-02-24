package project.movieslist.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.movieslist.model.Client;
import java.util.Collection;
import java.util.List;
@RequiredArgsConstructor
public class ClientSecurity implements UserDetails {
    private final Client client;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()->client.getRole().toString());
    }

    @Override
    public String getPassword() {
        return client.getPassword();
    }

    @Override
    public String getUsername() {
        return client.getUsername();
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
            return client.isEnabled();
    }
}
