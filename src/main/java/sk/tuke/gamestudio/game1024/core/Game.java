package sk.tuke.gamestudio.game1024.core;


public class Game {
    private Board board;
    private GameState gameState;
    private int score;

    public Game(int size) {
        this.board = new Board(size, this);
        this.gameState = GameState.PLAYING;
        this.score = 0;
    }

    public void startGame() {
        board.initializeBoard();
    }

    public void move(String direction) {
        if (gameState != GameState.PLAYING) return;

        board.moveTiles(direction);
        if (isWon()) {
            gameState = GameState.WON;
            System.out.println("Congratulations! You won!");
        } else if (isGameOver()) {
            gameState = GameState.LOST;
        }
    }

    public boolean isGameOver() {
        if (board.gameOver()) {
            gameState = GameState.LOST;
            return true;
        }
        return false;
    }

    public boolean isWon() {
        for (Tile[] row : board.getGrid()) {
            for (Tile tile : row) {
                if (tile.getValue() == 1024) {
                    gameState = GameState.WON;
                    return true;
                }
            }
        }
        return false;
    }

    public void restart() {
        board.initializeBoard();
        score = 0;
    }

    public int getScore() {
        return score;
    }

    public Board getBoard() {
        return board;
    }

    public void addScore(int points) {
        this.score += points;
    }
}