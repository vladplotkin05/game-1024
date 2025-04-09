package sk.tuke.gamestudio.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQuery(name = "Comment.getComments",
        query = "SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.commentedOn DESC")
@NamedQuery(name = "Comment.reset",
        query = "DELETE FROM Comment")
public class Comment implements Serializable {
    @Id
    @GeneratedValue
    private int ident;

    private String player;

    private String game;

    private String comment;

    private Date commentedOn;

    public Comment() {}

    public Comment(String player, String game, String comment, Date commentedOn) {
        this.player = player;
        this.game = game;
        this.comment = comment;
        this.commentedOn = commentedOn;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    public Date getCommentedOn() {return commentedOn;}

    public void setCommentedOn(Date commentedOn) {this.commentedOn = commentedOn;}


    @Override
    public String toString() {
        return "Comment{"  +
                "player='" + player + '\'' +
                ", game='" + game + '\'' +
                ", comment='" + comment + '\'' +
                ", commentedOn=" + commentedOn +
                '}';
    }
}
