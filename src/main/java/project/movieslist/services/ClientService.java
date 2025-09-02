package project.movieslist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.movieslist.model.Client;
import project.movieslist.model.Movie;
import project.movieslist.model.Review;
import project.movieslist.repositories.ClientRepository;
import project.movieslist.repositories.MovieRepository;
import project.movieslist.security.ClientSecurity;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService implements UserDetailsService {
    private final TMDbService tmDbService;
    private final ClientRepository clientRepository;
    private final MovieRepository movieRepository;
    @Autowired
    public ClientService(ClientRepository clientRepository, MovieRepository movieRepository, TMDbService tmDbService) {
        this.tmDbService=tmDbService;
        this.clientRepository = clientRepository;
        this.movieRepository = movieRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username){
        var client = clientRepository.findByUsername(username);
        return client.map(ClientSecurity::new)
                .orElseThrow(()->new UsernameNotFoundException("Username not found!"+username));
    }

    public Client addMovieToWatchedListByTitle(String username,String title, Double rating, String comment){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseGet(() -> {
                    Movie fetched = tmDbService.fetchMovieByTitle(title);
                    if (fetched == null) throw new RuntimeException("Movie not found in TMDb");
                    return movieRepository.save(fetched);
                });
        Review review=new Review();
        if(rating!=null)
        {
            movie.setRating(rating);
            review.setRating(rating);
        }
        if(comment!=null)
        {
            movie.setComment(comment);
            review.setComment(comment);
        }
        if(review.getComment()!=null && review.getRating()!=null){
            review.setAuthor(username);
            if(movie.getReviews()==null){
                List<Review> reviews=new ArrayList<>();
                reviews.add(review);
                movie.setReviews(reviews);
            }
            else{
                List<Review> reviews=movie.getReviews();
                reviews.add(review);
                movie.setReviews(reviews);
            }
            movieRepository.save(movie);
        }
        List<Movie> watched=client.getWatchedMovies();
        if(watched==null){
            watched=new ArrayList<>();
            client.setWatchedMovies(watched);
        }
        watched.add(movie);
        return clientRepository.save(client);

    }
    public Client addMoviesToWatchlistByTitle(String username,String title){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        Movie movie=movieRepository.findFirstByTitleIgnoreCase(title)
                .orElseThrow(()->new RuntimeException("Movie not found"));
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
        return clientRepository.save(client);

    }
    public Page<Movie> getWatchedMoviesByUsername(String username, Pageable pageable){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        List<Movie> watched=client.getWatchedMovies();
        if(watched==null){
            watched=new ArrayList<>();
        }
        int start=(int)pageable.getOffset();
        int end=Math.min(start+pageable.getPageSize(),watched.size());
        return new PageImpl<>(watched.subList(start,end),pageable,watched.size());
    }
    public Page<Movie> getWatchlistByUsername(String username, Pageable pageable){
        Client client=clientRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Client not found"));
        List<Movie> watchlist=client.getWatchList();
        if(watchlist==null){
            watchlist=new ArrayList<>();
        }
        int start=(int)pageable.getOffset();
        int end=Math.min(start+pageable.getPageSize(),watchlist.size());
        return new PageImpl<>(watchlist.subList(start,end),pageable,watchlist.size());
    }



}
