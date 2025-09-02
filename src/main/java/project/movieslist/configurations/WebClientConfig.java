package project.movieslist.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private final TMDbConfig tmDbConfig;
    public WebClientConfig(TMDbConfig tmDbConfig) {
        this.tmDbConfig = tmDbConfig;
    }
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(tmDbConfig.getApiUrl())
                .build();
    }
}
