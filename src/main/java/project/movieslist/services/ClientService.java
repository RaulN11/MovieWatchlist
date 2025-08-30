package project.movieslist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.repositories.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService implements UserDetailsService {
    private final ClientRepository clientRepository;
    private final MovieRepository movieRepository;
    @Autowired
    public ClientService(ClientRepository clientRepository, MovieRepository movieRepository) {
        this.clientRepository = clientRepository;
        this.movieRepository = movieRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client user = clientRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    public Client addMovieToWatchedListByTitle(String username,String title, Double rating, String comment){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseThrow(()->new RuntimeException("Movie not found"));
        if(rating!=null)  movie.setRating(rating);
        if(comment!=null) movie.setComment(comment);
        List<Movie> watched=client.getWatchedMovies();
        if(watched==null){
            watched=new ArrayList<>();
            client.setWatchedMovies(watched);
        }
        watched.add(movie);
        return clientRepository.save(client);

    }
    public List<Movie> getWatchedMoviesByUsername(String username){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        return client.getWatchedMovies();
    }


}
