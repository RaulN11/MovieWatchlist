package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.movieslist.configurations.TMDbConfig;
import project.movieslist.model.Actor;
import project.movieslist.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TMDbService {
    private final MovieService movieService;
    private final WebClient webClient;
    private final TMDbConfig tmDbConfig;

    private Map<String, Object> fetchMovieDetails(Integer movieId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", tmDbConfig.getApiKey())
                        .queryParam("append_to_response", "credits")
                        .build(movieId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private Movie mapToMovie(Map<String, Object> movieData, boolean fetchDetails) {
        Movie movie = new Movie();
        movie.setTitle((String) movieData.get("title"));

        String releaseDate = (String) movieData.getOrDefault("release_date", "");
        int year = 0;
        if (!releaseDate.isEmpty()) {
            try {
                year = Integer.parseInt(releaseDate.substring(0, 4));
            } catch (NumberFormatException ignored) {}
        }
        movie.setYear(year);

        Map<String, Object> details = fetchDetails ? fetchMovieDetails((Integer) movieData.get("id")) : movieData;

        String director = "";
        Map<String, Object> credits = (Map<String, Object>) details.get("credits");
        if (credits != null) {
            List<Map<String, Object>> crew = (List<Map<String, Object>>) credits.get("crew");
            if (crew != null) {
                director = crew.stream()
                        .filter(c -> "Director".equals(c.get("job")))
                        .map(c -> c.get("name").toString())
                        .findFirst()
                        .orElse("Unknown");
            }
            List<Map<String, Object>> cast = (List<Map<String, Object>>) credits.get("cast");
            if (cast != null) {
                List<Actor> actors=cast.stream()
                        .limit(10)
                        .map(c->{
                            Actor actor=new Actor();
                            actor.setId((Integer)c.get("id"));
                            actor.setFullName((String)c.get("name"));
                            actor.setPicture((String)c.get("profile_path"));
                            return actor;
                        })
                        .collect(Collectors.toList());
                movie.setActors(actors);
            }
        }
        movie.setDirector(director);

        List<Map<String, Object>> genres = (List<Map<String, Object>>) details.get("genres");
        if (genres != null) {
            String genreNames = genres.stream()
                    .map(g -> g.get("name").toString())
                    .collect(Collectors.joining(", "));
            movie.setGenre(genreNames);
        } else {
            movie.setGenre("Unknown");
        }

        movie.setPosterPath((String) details.getOrDefault("poster_path", ""));
        movie.setOverview((String)details.getOrDefault("overview", ""));
        return movie;
    }

    private List<Movie> fetchMoviesFromEndpoint(String path) {
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("api_key", tmDbConfig.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        List<Movie> movies = new ArrayList<>();

        for (Map<String, Object> result : results) {
            Movie movie = mapToMovie(result, true);
            movies.add(movie);

            if (!movieService.movieExists(movie)) {
                movieService.addMovie(movie);
            }
        }
        return movies;
    }

    public Movie fetchMovieByTitle(String title) {
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("api_key", tmDbConfig.getApiKey())
                        .queryParam("query", title)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        var results = (List<Map<String, Object>>) response.get("results");
        if (results.isEmpty()) return null;

        return mapToMovie(results.get(0), true);
    }

    public List<Movie> getTrendingMovies() {
        return fetchMoviesFromEndpoint("/movie/popular");
    }

    public List<Movie> getUpcomingMovies() {
        return fetchMoviesFromEndpoint("/movie/upcoming");
    }
}
