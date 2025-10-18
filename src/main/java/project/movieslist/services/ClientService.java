package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.repositories.MovieRepository;
import project.movieslist.security.ClientSecurity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService implements UserDetailsService {
    private final TMDbService tmDbService;
    private final ClientRepository clientRepository;
    private final MovieRepository movieRepository;
    @Override
    public UserDetails loadUserByUsername(String username){
        var client = clientRepository.findByUsername(username);
        return client.map(ClientSecurity::new)
                .orElseThrow(()->new UsernameNotFoundException("Username not found!"+username));
    }
    public Optional<Client> getUserByUsername(String username){
        return clientRepository.findByUsername(username);
    }

    public List<String> getAllUsernames() {
        return clientRepository.findAll().stream()
                .map(Client::getUsername)
                .collect(Collectors.toList());
    }

    public Client addMovieToWatchedListByTitle(String username,String title){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseGet(() -> {
                    Movie fetched = tmDbService.fetchAndSaveMovieByTitle(title);
                    if (fetched == null) throw new RuntimeException("Movie not found in TMDb");
                    return fetched;
                });
        List<Movie> watched=client.getWatchedMovies();
        if(watched==null){
            watched=new ArrayList<>();
            client.setWatchedMovies(watched);
        }
        watched.add(movie);
        movie.setWatchedCount(movie.getWatchedCount()+1);
        movieRepository.save(movie);
        return clientRepository.save(client);

    }
    public Client removeMovieFromWatchedListByTitle(String username,String title){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseGet(() -> {
                    Movie fetched = tmDbService.fetchMovieByTitle(title);
                    if (fetched == null) throw new RuntimeException("Movie not found in TMDb");
                    return fetched;
                });
        List<Movie>watched=client.getWatchedMovies();
        if(!watched.contains(movie)){
            throw new RuntimeException("Movie not found in watched list ");
        }else{
            watched.remove(movie);
            client.setWatchedMovies(watched);
            movie.setWatchedCount(movie.getWatchedCount()-1);
        }
        movieRepository.save(movie);
        return clientRepository.save(client);
    }
    public Client addMoviesToWatchlistByTitle(String username,String title){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseGet(()->{
                    Movie fetched = tmDbService.fetchAndSaveMovieByTitle(title);
                    if (fetched == null) throw new RuntimeException("Movie not found in TMDb");
                    return fetched;
                });
        List<Movie> watched=client.getWatchedMovies();
        if(watched.contains(movie)){
            throw new RuntimeException("Movie is already watched");
        }
        List<Movie> watchList=client.getWatchList();
        if(watchList==null){
            watchList=new ArrayList<>();
            client.setWatchList(watchList);
        }
        if(watchList.contains(movie)){
            throw new RuntimeException("Movie is already in the watchlist");
        }
        watchList.add(movie);
        movieRepository.save(movie);
        return clientRepository.save(client);

    }
    public Client removeMovieFromWatchlistListByTitle(String username,String title){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseGet(() -> {
                    Movie fetched = tmDbService.fetchMovieByTitle(title);
                    if (fetched == null) throw new RuntimeException("Movie not found in TMDb");
                    return fetched;
                });
        List<Movie>watchlist=client.getWatchList();
        if(!watchlist.contains(movie)){
            throw new RuntimeException("Movie not found in watchlist list ");
        }else{
            watchlist.remove(movie);
            client.setWatchList(watchlist);
        }
        movieRepository.save(movie);
        return clientRepository.save(client);
    }
    public Client addMovieToLikedByTitle(String username,String title){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseGet(()->{
                    Movie fetched = tmDbService.fetchAndSaveMovieByTitle(title);
                    if (fetched == null) throw new RuntimeException("Movie not found in TMDb");
                    return fetched;
                });
        List<Movie> liked=client.getLikedMovies();
        if(liked==null){
            liked=new ArrayList<>();
            client.setLikedMovies(liked);
        }
        if(liked.contains(movie)){
            throw new RuntimeException("Movie is already in the liked list");
        }
        liked.add(movie);
        movie.setLikedCount(movie.getLikedCount()+1);
        movieRepository.save(movie);
        return clientRepository.save(client);
    }
    public Client removeMovieFromLikedListByTitle(String username,String title){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseGet(() -> {
                    Movie fetched = tmDbService.fetchMovieByTitle(title);
                    if (fetched == null) throw new RuntimeException("Movie not found in TMDb");
                    return fetched;
                });
        List<Movie>liked=client.getLikedMovies();
        if(!liked.contains(movie)){
            throw new RuntimeException("Movie not found in liked list ");
        }else{
            liked.remove(movie);
            client.setLikedMovies(liked);
            movie.setLikedCount(movie.getLikedCount()-1);
        }
        movieRepository.save(movie);
        return clientRepository.save(client);
    }
    public List<Movie> getWatchedMoviesByUsername(String username){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        List<Movie> watched=client.getWatchedMovies();
        if(watched==null){
            watched=new ArrayList<>();
        }
        return watched;
    }
    public List<Movie> getWatchlistByUsername(String username){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        List<Movie> watchlist=client.getWatchList();
        if(watchlist==null){
            watchlist=new ArrayList<>();
        }
        return watchlist;
    }
    public List<Movie> getLikedByUsername(String username){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        List<Movie> liked=client.getLikedMovies();
        if (liked==null){
            liked=new ArrayList<>();
        }
        return liked;
    }
    public Client addToFollowing(String c1, String c2){
        Client client1=clientRepository.findByUsername(c1)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Client client2=clientRepository.findByUsername(c2)
                .orElseThrow(()->new RuntimeException("Client not found"));
        List<String> followingC1=client1.getFollowing();
        if(followingC1==null){
            followingC1=new ArrayList<>();
            followingC1.add(client2.getUsername());
            client1.setFollowing(followingC1);
        }
        else{
            followingC1.add(client2.getUsername());
        }
        List<String> followersc2=client2.getFollowers();
        if(followersc2==null){
            followersc2=new ArrayList<>();
            followersc2.add(client1.getUsername());
            client2.setFollowers(followersc2);
        }
        else{
            followersc2.add(client1.getUsername());
        }
        clientRepository.save(client2);
        return clientRepository.save(client1);
    }
    public Client addProfilePicture(Client client,String url){
        client.setProfilePicture(url);
        return clientRepository.save(client);
    }
    public Client addBio(Client client, String bio){
        client.setBio(bio);
        return clientRepository.save(client);
    }
    public Client addCity(Client client, String city){
        client.setCity(city);
        return clientRepository.save(client);
    }
    public Client addCountry(Client client, String country){
        client.setCountry(country);
        return clientRepository.save(client);
    }
}