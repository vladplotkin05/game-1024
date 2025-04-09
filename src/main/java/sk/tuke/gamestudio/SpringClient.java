package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game1024.consoleUI.Console;
import sk.tuke.gamestudio.game1024.core.Game;
import sk.tuke.gamestudio.service.*;


@Configuration
@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "sk.tuke.gamestudio.server.*"))
public class SpringClient {
    public static void main(String[] args) { new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);}

    @Bean
    public CommandLineRunner runner(Console console) {
        return s -> console.play(game());
    }

    @Bean
    public Console console() {
        return new Console();
    }

    @Bean
    public Game game() {
        return new Game(4);
    }

    @Bean
    public ScoreService scoreService(){
        return new ScoreServiceRestClient();
    }

    @Bean
    public RatingService ratingService(){ return new RatingServiceRestClient(); }

    @Bean
    public CommentService commentService(){ return new CommentServiceRestClient(); }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
