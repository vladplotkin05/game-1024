import org.junit.jupiter.api.*;
import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.service.CommentServiceJDBC;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {
    private static final String URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASSWORD = "P10gle_Pwd";

    private CommentServiceJDBC commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceJDBC();
        commentService.reset();
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM comment");
        }
    }

    @Test
    void testAddComment() {
        Comment comment = new Comment("miki", "game1024", "Super game!", new Date());
        commentService.addComment(comment);

        List<Comment> comments = commentService.getComments("game1024");
        assertEquals(1, comments.size(), "Should be exactly 1 comment in the database");
        assertEquals("miki", comments.get(0).getPlayer());
        assertEquals("game1024", comments.get(0).getGame());
        assertEquals("Super game!", comments.get(0).getComment());
    }

    @Test
    void testGetComments() {
        commentService.addComment(new Comment("player1", "Tetris", "Awesome!", new Date()));
        commentService.addComment(new Comment("player2", "Tetris", "Loved it!", new Date()));
        commentService.addComment(new Comment("player3", "Pacman", "Fun game!", new Date()));

        List<Comment> comments = commentService.getComments("Tetris");
        assertEquals(2, comments.size(), "Should return exactly 2 comments for Tetris");

        assertTrue(comments.stream().anyMatch(c -> c.getPlayer().equals("player1") && c.getComment().equals("Awesome!")));
        assertTrue(comments.stream().anyMatch(c -> c.getPlayer().equals("player2") && c.getComment().equals("Loved it!")));
    }

    @Test
    void testReset() {
        commentService.addComment(new Comment("anna", "game1024", "Nice!", new Date()));
        commentService.addComment(new Comment("veronika", "game1024", "Amazing!", new Date()));

        commentService.reset();

        List<Comment> comments = commentService.getComments("game1024");
        assertEquals(0, comments.size(), "All comments should be deleted after reset");
    }
}
