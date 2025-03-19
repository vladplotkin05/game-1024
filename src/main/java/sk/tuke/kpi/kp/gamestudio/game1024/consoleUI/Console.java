package sk.tuke.kpi.kp.gamestudio.game1024.consoleUI;
import sk.tuke.kpi.kp.gamestudio.entity.Comment;
import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import sk.tuke.kpi.kp.gamestudio.entity.Score;
import sk.tuke.kpi.kp.gamestudio.game1024.core.Board;
import sk.tuke.kpi.kp.gamestudio.game1024.core.Game;
import sk.tuke.kpi.kp.gamestudio.game1024.core.Tile;
import sk.tuke.kpi.kp.gamestudio.service.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class Console {
    private Game game;
    private Scanner scanner = new Scanner(System.in);
    private ScoreService scoreService = new ScoreServiceJDBC();
    private RatingService ratingService = new RatingServiceJDBC();
    private CommentService commentService = new CommentServiceJDBC();


    public void play(Game game) {
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) {
            playerName = "Unknown";
        }
        printTopScores();
        printComments();
        this.game = game;
        game.startGame();

        while (!game.isGameOver() && !game.isWon()) {
            show();
            handleInput();
        }

        int scorePoints = game.getScore();
        Score score = new Score(playerName, "game1024", scorePoints, new Date());
        scoreService.addScore(score);
        if (game.isWon()) {
            show();
            System.out.println("Congratulations! You won!");
        } else {
            System.out.println("Game over! Try again.");
        }
        askForRating(playerName);
        askForComment(playerName);
        if (askForRestart()) {
            play(new Game(game.getBoard().getGrid().length));
        }
    }

    private void show() {
        Board board = game.getBoard();
        Tile[][] grid = board.getGrid();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Tile tile = grid[i][j];
                if (tile.getValue() == 0) {
                    System.out.print("-\t");
                } else {
                    System.out.print(tile.getValue() + "\t");
                }
            }
            System.out.println();
        }
        System.out.println("Score: " + game.getScore() + " | Game Rating: " + ratingService.getAverageRating("game1024"));
    }

    private void handleInput() {
        System.out.print("Enter move (WASD for direction, Q to quit): ");
        String input = scanner.nextLine().toUpperCase();

        switch (input) {
            case "W":
                game.move("UP");
                break;
            case "A":
                game.move("LEFT");
                break;
            case "S":
                game.move("DOWN");
                break;
            case "D":
                game.move("RIGHT");
                break;
            case "Q":
                System.out.println("Exiting game...");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid input! Use W, A, S, D or Q.");
        }
    }

    private boolean askForRestart() {
        System.out.print("Do you want to start a new game? (Y/N): ");
        String response = scanner.nextLine().toUpperCase();
        if (response.equals("Y")) {
            game.restart();
            return true;
        }
        return false;
    }

    private void printTopScores() {
        List<Score> scores = scoreService.getTopScores("game1024");
        int raiting = ratingService.getAverageRating("game1024");
        System.out.println("------------------------------------------------------------");
        System.out.println("Top Scores for game1024 | Average Rating: " + raiting);
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            System.out.printf("%d. %s %d\n", i+1, score.getPlayer(), score.getPoints());
        }
        System.out.println("------------------------------------------------------------");
    }

    private void printComments() {
        List<Comment> comments = commentService.getComments("game1024");
        System.out.println("------------------------------------------------------------");
        System.out.println("Recent Comments for game1024:");
        System.out.println("------------------------------------------------------------");
        for (Comment comment : comments) {
            System.out.printf("%s: %s (%s)\n", comment.getPlayer(), comment.getComment(), comment.getCommentedOn());
        }
        System.out.println("------------------------------------------------------------");
    }

    private void askForRating(String playerName) {
        System.out.print("Rate the game from 1 to 5: ");
        try {
            int rating = Integer.parseInt(scanner.nextLine());
            if (rating >= 1 && rating <= 5) {
                ratingService.setRating(new Rating(playerName, "game1024", rating, new Date()));
                System.out.println("Thank you for rating!");
            } else {
                System.out.println("Invalid rating. Must be between 1 and 5.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Rating not saved.");
        }
    }

    private void askForComment(String playerName) {
        System.out.print("Leave a comment about the game: ");
        String commentText = scanner.nextLine().trim();
        if (!commentText.isEmpty()) {
            commentService.addComment(new Comment(playerName, "game1024", commentText, new Date()));
            System.out.println("Thank you for your comment!");
        }
    }
}

