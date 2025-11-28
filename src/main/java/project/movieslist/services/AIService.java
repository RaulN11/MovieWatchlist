package project.movieslist.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import project.movieslist.model.Movie;

import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private final ChatClient chatClient;

    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    public String generate(String prompt){
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
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
}