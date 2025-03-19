package sk.tuke.kpi.kp.gamestudio.game1024.core;

import java.util.Random;

public class Board {
    private Tile[][] grid;
    private int size;
    private Game game;
    private Random random = new Random();

    public Board(int size, Game game) {
        this.size = size;
        this.game = game;
        this.grid = new Tile[size][size];
        initializeBoard();
    }

    public void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Tile(0);
            }
        }
       // grid[0][0].setValue(1024);

        addRandomTile();
        addRandomTile();
    }

    public void addRandomTile() {
        int emptyCount = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].getValue() == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount == 0) return;

        int randomIndex = random.nextInt(emptyCount);
        int counter = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].getValue() == 0) {
                    if (counter == randomIndex) {
                        grid[i][j].setValue(random.nextDouble() < 0.9 ? 2 : 4);
                        return;
                    }
                    counter++;
                }
            }
        }
    }

    public void moveTiles(String direction) {
        switch (direction) {
            case "UP":
                moveUp();
                break;
            case "DOWN":
                moveDown();
                break;
            case "LEFT":
                moveLeft();
                break;
            case "RIGHT":
                moveRight();
                break;
        }
        addRandomTile();
    }

    private void moveUp() {
        for (int j = 0; j < size; j++) {
            for (int i = 1; i < size; i++) {
                if (grid[i][j].getValue() != 0) {
                    int row = i;
                    while (row > 0 && grid[row - 1][j].getValue() == 0) {
                        grid[row - 1][j].setValue(grid[row][j].getValue());
                        grid[row][j].setValue(0);
                        row--;
                    }
                    if (row > 0 && grid[row - 1][j].getValue() == grid[row][j].getValue()) {
                        grid[row - 1][j].doubleValue();
                        if (game != null) {
                            game.addScore(grid[row - 1][j].getValue());
                        }
                        grid[row][j].setValue(0);
                    }
                }
            }
        }
    }

    private void moveDown() {
        for (int j = 0; j < size; j++) {
            for (int i = size - 2; i >= 0; i--) {
                if (grid[i][j].getValue() != 0) {
                    int row = i;
                    while (row < size - 1 && grid[row + 1][j].getValue() == 0) {
                        grid[row + 1][j].setValue(grid[row][j].getValue());
                        grid[row][j].setValue(0);
                        row++;
                    }
                    if (row < size - 1 && grid[row + 1][j].getValue() == grid[row][j].getValue()) {
                        grid[row + 1][j].doubleValue();
                        if (game != null) {
                            game.addScore(grid[row + 1][j].getValue());
                        }
                        grid[row][j].setValue(0);
                    }
                }
            }
        }
    }

    private void moveLeft() {
        for (int i = 0; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if (grid[i][j].getValue() != 0) {
                    int col = j;
                    while (col > 0 && grid[i][col - 1].getValue() == 0) {
                        grid[i][col - 1].setValue(grid[i][col].getValue());
                        grid[i][col].setValue(0);
                        col--;
                    }
                    if (col > 0 && grid[i][col - 1].getValue() == grid[i][col].getValue()) {
                        grid[i][col - 1].doubleValue();
                        if (game != null) {
                            game.addScore(grid[i][col - 1].getValue());
                        }
                        grid[i][col].setValue(0);
                    }
                }
            }
        }
    }

    private void moveRight() {
        for (int i = 0; i < size; i++) {
            for (int j = size - 2; j >= 0; j--) {
                if (grid[i][j].getValue() != 0) {
                    int col = j;
                    while (col < size - 1 && grid[i][col + 1].getValue() == 0) {
                        grid[i][col + 1].setValue(grid[i][col].getValue());
                        grid[i][col].setValue(0);
                        col++;
                    }
                    if (col < size - 1 && grid[i][col + 1].getValue() == grid[i][col].getValue()) {
                        grid[i][col + 1].doubleValue();
                        if (game != null) {
                            game.addScore(grid[i][col + 1].getValue());
                        }
                        grid[i][col].setValue(0);
                    }
                }
            }
        }
    }

    public boolean gameOver() {
        if (!isFull()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int currentValue = grid[i][j].getValue();

                if ((i < size - 1 && grid[i + 1][j].getValue() == currentValue) ||  //down
                        (j < size - 1 && grid[i][j + 1].getValue() == currentValue) ||  //right
                        (i > 0 && grid[i - 1][j].getValue() == currentValue) ||  // up
                        (j > 0 && grid[i][j - 1].getValue() == currentValue)) {  // left
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].getValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public Tile[][] getGrid() {
        return grid;
    }
}
