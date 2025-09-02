package project.movieslist.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.movieslist.model.Client;

import java.util.Collection;
import java.util.List;

public class ClientSecurity implements UserDetails {
    private final Client client;
    public ClientSecurity(Client client) {
        this.client = client;
    }
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
        return true;
    }
}
