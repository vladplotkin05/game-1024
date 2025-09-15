package sk.tuke.gamestudio.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.game1024.core.Game;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class Game1024Controller {
    private Game game = new Game(4);

    @RequestMapping("/game1024")
    public String game1024(
            @RequestParam(value = "move", required = false) String move,
            Model model
    ) {
        if (game == null) {
            game = new Game(4);
            game.startGame();
        }

        if (move != null && !move.isEmpty()) {
            game.move(move.toUpperCase());
        }

        model.addAttribute("grid", game.getBoard().getGrid());
        model.addAttribute("score", game.getScore());
        model.addAttribute("gameOver", game.isGameOver());
        model.addAttribute("won", game.isWon());

        return "game1024";
    }


    @RequestMapping("/game1024/new")
    public String newGame() {
        game = new Game(4);
        game.startGame();
        return "redirect:/game1024";
    }

}
