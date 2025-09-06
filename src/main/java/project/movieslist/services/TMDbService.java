package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.movieslist.configurations.TMDbConfig;
import project.movieslist.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TMDbService {
    private final WebClient webClient;
    private final TMDbConfig tmDbConfig;
    public Movie fetchMovieByTitle(String title) {
        Map<String, Object> response = webClient
                .get()
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

        Map<String, Object> result = results.get(0);
        Movie movie = new Movie();
        movie.setTitle((String) result.get("title"));

        String releaseDate = (String) result.getOrDefault("release_date", "");
        int year = 0;
        if (!releaseDate.isEmpty()) {
            try {
                year = Integer.parseInt(releaseDate.substring(0, 4));
            } catch (NumberFormatException e) {
                year = 0;
            }
        }
        movie.setYear(year);
        Integer movieId = (Integer) result.get("id");
        Map<String, Object> details = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", tmDbConfig.getApiKey())
                        .queryParam("append_to_response", "credits")
                        .build(movieId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        String director = "";
        Map<String, Object> credits = (Map<String, Object>) details.get("credits");
        if (credits != null) {
            List<Map<String, Object>> crew = (List<Map<String, Object>>) credits.get("crew");
            if (crew != null) {
                director = crew.stream()
                        .filter(c -> "Director".equals(c.get("job")))
                        .map(c -> c.get("name").toString())
                        .findFirst()
                        .orElse("");
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
            movie.setGenre("");
        }
        movie.setPosterPath((String) details.getOrDefault("poster_path", ""));

        return movie;
    }


}
