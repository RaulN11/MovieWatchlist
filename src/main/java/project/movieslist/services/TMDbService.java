package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.movieslist.configurations.TMDbConfig;
import project.movieslist.model.Actor;
import project.movieslist.model.Movie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TMDbService {
    private final MovieService movieService;
    private final WebClient webClient;
    private final TMDbConfig tmDbConfig;

    private Movie mapToMovie(Map<String, Object> movieData) {
        Movie movie = new Movie();
        movie.setTid((Integer) movieData.get("id"));
        movie.setTitle((String) movieData.get("title"));

        String releaseDate = (String) movieData.getOrDefault("release_date", "");
        int year = 0;
        if (!releaseDate.isEmpty()) {
            try {
                year = Integer.parseInt(releaseDate.substring(0, 4));
            } catch (NumberFormatException ignored) {}
        }
        movie.setYear(year);

        String director = "";
        Map<String, Object> credits = (Map<String, Object>) movieData.get("credits");
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
                List<Actor> actors = cast.stream()
                        .limit(10)
                        .map(c -> {
                            Actor actor = new Actor();
                            actor.setId((Integer) c.get("id"));
                            actor.setFullName((String) c.get("name"));
                            actor.setPicture((String) c.get("profile_path"));
                            return actor;
                        })
                        .collect(Collectors.toList());
                movie.setActors(actors);
            }
        }
        movie.setDirector(director);

        List<Map<String, Object>> genres = (List<Map<String, Object>>) movieData.get("genres");
        if (genres != null) {
            String genreNames = genres.stream()
                    .map(g -> g.get("name").toString())
                    .collect(Collectors.joining(", "));
            movie.setGenre(genreNames);
        } else {
            movie.setGenre("Unknown");
        }

        Object runtimeObj = movieData.get("runtime");
        if (runtimeObj instanceof Number) {
            int totalMinutes = ((Number) runtimeObj).intValue();
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            movie.setRuntime(String.format("%dh %02dm", hours, minutes));
        } else {
            movie.setRuntime("Unknown");
        }
        movie.setPosterPath((String) movieData.getOrDefault("poster_path", ""));
        movie.setBackdropPath((String) movieData.getOrDefault("backdrop_path", ""));
        movie.setPopularity((Double) movieData.getOrDefault("popularity", 0));
        movie.setOverview((String) movieData.getOrDefault("overview", ""));
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
            Integer movieId = (Integer) result.get("id");
            Map<String, Object> fullDetails = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/{id}")
                            .queryParam("api_key", tmDbConfig.getApiKey())
                            .queryParam("append_to_response", "credits")
                            .build(movieId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Movie movie = mapToMovie(fullDetails);
            movies.add(movie);
        }
        return movies;
    }

    public List<Movie> fetchMoviesByTitle(String title) {
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

        List<Movie> movies = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Integer movieId = (Integer) result.get("id");
            Map<String, Object> fullDetails = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/{id}")
                            .queryParam("api_key", tmDbConfig.getApiKey())
                            .queryParam("append_to_response", "credits")
                            .build(movieId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            movies.add(mapToMovie(fullDetails));
        }
        movies=movies.stream()
                .sorted(Comparator.comparing(Movie::getPopularity).reversed())
                .collect(Collectors.toList());
        return movies;
    }

    public Movie fetchMovieByTid(Integer tid) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/{id}")
                            .queryParam("api_key", tmDbConfig.getApiKey())
                            .queryParam("append_to_response", "credits")
                            .build(tid))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return mapToMovie(response);
        } catch (Exception e) {
            System.err.println("Error fetching movie by ID: " + e.getMessage());
            return null;
        }
    }

    public List<Movie> fetchMovieByActorName(String actorName) {
        List<Actor> actors = fetchActorsByName(actorName);
        Actor actor = actors.get(0);
        String actorId = actor.getId().toString();

        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/person/{id}/movie_credits")
                        .queryParam("api_key", tmDbConfig.getApiKey())
                        .build(actorId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> cast = (List<Map<String, Object>>) response.get("cast");
        if (cast == null) {
            return List.of();
        }

        return cast.stream()
                .map(movieData -> {
                    Integer movieId = (Integer) movieData.get("id");
                    Map<String, Object> fullDetails = webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/movie/{id}")
                                    .queryParam("api_key", tmDbConfig.getApiKey())
                                    .queryParam("append_to_response", "credits")
                                    .build(movieId))
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();
                    return mapToMovie(fullDetails);
                })
                .sorted(Comparator.comparing(Movie::getYear).reversed())
                .collect(Collectors.toList());
    }

    public Movie fetchAndSaveMovieByTid(Integer tid) {
        Movie movie = fetchMovieByTid(tid);
        if (movie != null && !movieService.movieExists(movie)) {
            movieService.addMovie(movie);
        }
        return movie;
    }

    public List<Movie> getTrendingMovies() {
        return fetchMoviesFromEndpoint("/movie/popular");
    }

    public List<Movie> getUpcomingMovies() {
        return fetchMoviesFromEndpoint("/movie/upcoming");
    }

    public List<Actor> fetchActorsByName(String name) {
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/person")
                        .queryParam("api_key", tmDbConfig.getApiKey())
                        .queryParam("query", name)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results.isEmpty()) return null;

        List<Actor> actors = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Integer actorId=(Integer)result.get("id");
            Map<String, Object> details=fetchActorDetails(actorId);
            Actor actor = new Actor();
            actor.setId((Integer) result.get("id"));
            actor.setFullName((String) result.get("name"));
            actor.setPicture((String) result.get("profile_path"));
            actor.setBirthDate((String) details.getOrDefault("birthday", null));
            actor.setPopularity((Double) details.getOrDefault("popularity", 0));
            actor.setBirthPlace((String) details.getOrDefault("place_of_birth", null));
            actors.add(actor);
        }
        List<Actor> sortedActors=actors.stream()
                .sorted(Comparator.comparing(Actor::getPopularity).reversed())
                .limit(15)
                .collect(Collectors.toList());

        return sortedActors;
    }
    public Map<String, Object> fetchActorDetails(Integer actorId){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/person/{id}")
                        .queryParam("api_key",tmDbConfig.getApiKey())
                        .build(actorId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}