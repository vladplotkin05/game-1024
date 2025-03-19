import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.entity.Score;
import sk.tuke.kpi.kp.gamestudio.service.*;

import java.util.Date;
import java.util.List;

public class TestJDBC {
    public static void main(String[] args){
        /// TESTING SCORE SERVICE
        ScoreService service = new ScoreServiceJDBC();
        service.reset();
        service.addScore(new Score("vadim", "game1024", 204, new Date()));
        service.addScore(new Score("vadim", "game1024", 150, new Date()));
        service.addScore(new Score("viktor", "game1024", 180, new Date()));
        service.addScore(new Score("vlad", "mines", 200, new Date()));

        List<Score> scores = service.getTopScores("game1024");
        System.out.println(scores);

        /// testing COMMENT SERVIcE
        CommentService commentService = new CommentServiceJDBC();
        commentService.reset();
        commentService.addComment(new Comment("vadim", "game1024", "Cool game!", new Date()));
        commentService.addComment(new Comment("viktor", "game1024", "Very difficult!", new Date()));

        List<Comment> comments = commentService.getComments("game1024");
        System.out.println("Comments:");
        System.out.println(comments);

        /// TESTING RATING SERVICE
        RatingService ratingService = new RatingServiceJDBC();
        ratingService.reset();
        ratingService.setRating(new Rating("denis", "game1024", 2, new Date()));
        ratingService.setRating(new Rating("viktor", "game1024", 5, new Date()));
        ratingService.setRating(new Rating("cristian", "game1024", 1, new Date()));

        int ratingCristian = ratingService.getRating("cristian", "game1024");
        System.out.println("Cristian's rating for game1024: " + ratingCristian);

        int averageRating = ratingService.getAverageRating("game1024");
        System.out.println("Average rating for game1024: " + averageRating);

    }
}
