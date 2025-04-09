package sk.tuke.gamestudio.game1024;


import sk.tuke.gamestudio.game1024.consoleUI.Console;
import sk.tuke.gamestudio.game1024.core.Game;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to 1024! Use W, A, S, D to move and Q to quit.");

        int size;
        do {
            System.out.print("Please enter grid size (minimum 3): ");
            size = scanner.nextInt();
            if (size < 3) {
                System.out.println("Invalid size! Grid size must be at least 3.");
            }
        } while (size < 3);

        Game game = new Game(size);
        Console console = new Console();
        console.play(game);
    }
}
