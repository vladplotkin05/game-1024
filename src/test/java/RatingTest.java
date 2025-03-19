import org.junit.jupiter.api.*;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.service.RatingServiceJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import sk.tuke.kpi.kp.gamestudio.service.RatingException;
import static org.junit.jupiter.api.Assertions.*;

public class RatingTest {
    private static final String URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASSWORD = "P10gle_Pwd";

    private RatingServiceJDBC ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingServiceJDBC();
        ratingService.reset();
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM rating");
        }
    }

    @Test
    void testSetRating() {
        Rating rating = new Rating("vera", "game1024", 5, new Date());
        ratingService.setRating(rating);

        int retrievedRating = ratingService.getRating("vera", "game1024");
        assertEquals(5, retrievedRating, "Rating should be correctly inserted and retrieved");
    }

    @Test
    void testGetAverageRating() {
        ratingService.setRating(new Rating("vera", "game1024", 4, new Date()));
        ratingService.setRating(new Rating("linda", "game1024", 5, new Date()));

        int averageRating = ratingService.getAverageRating("game1024");
        assertEquals(5, averageRating, "Average rating should be rounded to the nearest integer");
    }

    @Test
    void testGetRating_PlayerHasNotRated() {
        assertThrows(RatingException.class, () -> ratingService.getRating("vera", "game1024"),
                "Should throw exception if player has not rated the game");
    }

    @Test
    void testReset() {
        ratingService.setRating(new Rating("anna", "game1024", 3, new Date()));
        ratingService.setRating(new Rating("veronika", "game1024", 5, new Date()));

        ratingService.reset();

        assertThrows(RatingException.class,
                () -> ratingService.getRating("anna", "game1024"),
                "Should throw RatingException because all ratings should be deleted");
    }
}
