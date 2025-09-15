package sk.tuke.gamestudio.game1024.core;

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
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = new Tile(0);
            }
        }

        //grid[0][0].setValue(1024);

        addRandomTile();
        addRandomTile();
    }

    public void addRandomTile() {
        int emptyCount = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col].getValue() == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount == 0) return;

        int randomIndex = random.nextInt(emptyCount);
        int counter = 0;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col].getValue() == 0) {
                    if (counter == randomIndex) {
                        grid[row][col].setValue(random.nextDouble() < 0.9 ? 2 : 4);
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
        for (int col = 0; col < size; col++) {
            for (int row = 1; row < size; row++) {
                if (grid[row][col].getValue() != 0) {
                    int currentRow = row;
                    while (currentRow > 0 && grid[currentRow - 1][col].getValue() == 0) {
                        grid[currentRow - 1][col].setValue(grid[currentRow][col].getValue());
                        grid[currentRow][col].setValue(0);
                        currentRow--;
                    }
                    if (currentRow > 0 && grid[currentRow - 1][col].getValue() == grid[currentRow][col].getValue()) {
                        grid[currentRow - 1][col].doubleValue();
                        if (game != null) {
                            game.addScore(grid[currentRow - 1][col].getValue());
                        }
                        grid[currentRow][col].setValue(0);
                    }
                }
            }
        }
    }

    private void moveDown() {
        for (int col = 0; col < size; col++) {
            for (int row = size - 2; row >= 0; row--) {
                if (grid[row][col].getValue() != 0) {
                    int currentRow = row;
                    while (currentRow < size - 1 && grid[currentRow + 1][col].getValue() == 0) {
                        grid[currentRow + 1][col].setValue(grid[currentRow][col].getValue());
                        grid[currentRow][col].setValue(0);
                        currentRow++;
                    }
                    if (currentRow < size - 1 && grid[currentRow + 1][col].getValue() == grid[currentRow][col].getValue()) {
                        grid[currentRow + 1][col].doubleValue();
                        if (game != null) {
                            game.addScore(grid[currentRow + 1][col].getValue());
                        }
                        grid[currentRow][col].setValue(0);
                    }
                }
            }
        }
    }

    private void moveLeft() {
        for (int row = 0; row < size; row++) {
            for (int col = 1; col < size; col++) {
                if (grid[row][col].getValue() != 0) {
                    int currentCol = col;
                    while (currentCol > 0 && grid[row][currentCol - 1].getValue() == 0) {
                        grid[row][currentCol - 1].setValue(grid[row][currentCol].getValue());
                        grid[row][currentCol].setValue(0);
                        currentCol--;
                    }
                    if (currentCol > 0 && grid[row][currentCol - 1].getValue() == grid[row][currentCol].getValue()) {
                        grid[row][currentCol - 1].doubleValue();
                        if (game != null) {
                            game.addScore(grid[row][currentCol - 1].getValue());
                        }
                        grid[row][currentCol].setValue(0);
                    }
                }
            }
        }
    }

    private void moveRight() {
        for (int row = 0; row < size; row++) {
            for (int col = size - 2; col >= 0; col--) {
                if (grid[row][col].getValue() != 0) {
                    int currentCol = col;
                    while (currentCol < size - 1 && grid[row][currentCol + 1].getValue() == 0) {
                        grid[row][currentCol + 1].setValue(grid[row][currentCol].getValue());
                        grid[row][currentCol].setValue(0);
                        currentCol++;
                    }
                    if (currentCol < size - 1 && grid[row][currentCol + 1].getValue() == grid[row][currentCol].getValue()) {
                        grid[row][currentCol + 1].doubleValue();
                        if (game != null) {
                            game.addScore(grid[row][currentCol + 1].getValue());
                        }
                        grid[row][currentCol].setValue(0);
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
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col].getValue() == 0) {
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
