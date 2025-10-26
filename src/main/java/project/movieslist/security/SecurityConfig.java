package project.movieslist.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import project.movieslist.services.ClientService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig {
    private final ClientService clientService;

    @Bean
    public UserDetailsService userDetailsService() {
        return clientService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(clientService);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                AuthenticationException exception) -> {
            String errorMessage = "Invalid credentials";

            if (exception instanceof DisabledException) {
                errorMessage = "Account not verified. Please check your email and verify your account.";
            }

            request.getSession().setAttribute("error", errorMessage);
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(httpForm -> {
                    httpForm.loginPage("/login").permitAll();
                    httpForm.defaultSuccessUrl("/homepage");
                    httpForm.failureHandler(authenticationFailureHandler());
                })
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(
                            "/login",
                            "/signup",
                            "/signup/verify",
                            "/css/**",
                            "/js/**",
                            "/ws/**",
                            "/homepage",
                            "/details/**",
                            "/searchMenu/**",
                            "/moviesbyactor/**"
                    ).permitAll();
                    registry.requestMatchers(
                            "/watched",
                            "/watchlist",
                            "/liked",
                            "/profile",
                            "/client/**",
                            "/chat",
                            "/messages/**",
                            "/users"
                    ).authenticated();
                    registry.anyRequest().authenticated();
                })
                .build();
    }
}