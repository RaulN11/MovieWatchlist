package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.movieslist.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {
    private final ChatClient chatClient;
    @Autowired
    private TMDbService tmDbService;

    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateRecommendations(Map<Integer, Movie> favouriteMovies){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Give me a list of 10 movies, only the names of the movies, no other remarks, that would fit the taste of the user whose top 3 movies are. Also, recommend movies from the directors the user likes, different genres than the ones in the top:");
        for(Movie movie:favouriteMovies.values()){
            stringBuilder.append(movie.getTitle()).append(", ");
        }
        String prompt=stringBuilder.toString();
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    public List<Movie> parseAndFetchMovies(String aiResponse) {
        List<String> movieTitles = Arrays.stream(aiResponse.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.replaceAll("^[\\d]+[.)\\s]*", "")
                        .replaceAll("^[-*•]\\s*", "")
                        .trim())
                .filter(title -> !title.isEmpty())
                .limit(10)
                .collect(Collectors.toList());

        List<Movie> movies = new ArrayList<>();
        for (String title : movieTitles) {
            try {
                List<Movie> searchResults = tmDbService.fetchMoviesByTitle(title);
                if (searchResults != null && !searchResults.isEmpty()) {
                    movies.add(searchResults.get(0));
                }
            } catch (Exception e) {
                System.err.println("Error fetching movie: " + title);
            }
        }

        return movies;
    }
}