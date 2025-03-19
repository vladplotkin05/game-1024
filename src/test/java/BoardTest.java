import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.gamestudio.game1024.core.Board;
import sk.tuke.kpi.kp.gamestudio.game1024.core.Game;
import sk.tuke.kpi.kp.gamestudio.game1024.core.Tile;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private Board board;
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(4);
        board = game.getBoard();
    }

    @Test
    void testBoardInitialization() {
        Tile[][] grid = board.getGrid();
        int emptyTiles = 0;
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                if (tile.getValue() == 0) {
                    emptyTiles++;
                }
            }
        }
        assertEquals(14, emptyTiles, "Board should be initialized with exactly 2 non-empty tiles.");
    }

    @Test
    void testAddRandomTile() {
        int before = countNonEmptyTiles();
        board.addRandomTile();
        int after = countNonEmptyTiles();
        assertEquals(before + 1, after, "One tile should be added.");
    }

    private int countNonEmptyTiles() {
        int count = 0;
        for (Tile[] row : board.getGrid()) {
            for (Tile tile : row) {
                if (tile.getValue() != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    @Test
    void testMoveLeft() {
        board.getGrid()[0][0].setValue(2);
        board.getGrid()[0][1].setValue(2);
        board.moveTiles("LEFT");
        assertEquals(4, board.getGrid()[0][0].getValue(), "Tiles should merge to 4");
        assertEquals(0, board.getGrid()[0][1].getValue(), "Second tile should be empty");
    }

    @Test
    void testMoveRight() {
        board.getGrid()[0][2].setValue(2);
        board.getGrid()[0][3].setValue(2);
        board.moveTiles("RIGHT");
        assertEquals(4, board.getGrid()[0][3].getValue(), "Tiles should merge to 4");
        assertEquals(0, board.getGrid()[0][2].getValue(), "Second tile should be empty");
    }

    @Test
    void testGameOver() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board.getGrid()[i][j].setValue((i + j) % 2 == 0 ? 2 : 4);
            }
        }
        assertTrue(board.gameOver(), "Game should be over when no moves are left.");
    }
}
