package brrrr.go.horsey.orm;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Carried over from chess - some way to represent a connect-four board using a string
    @Column
    private String FEN;

    @Column
    private Integer turn;

    @JoinColumn
    @ManyToOne
    private Game game;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFEN() {
        return FEN;
    }

    public void setFEN(String FEN) {
        this.FEN = FEN;
    }

    public Integer getTurn() {
        return turn;
    }
    public void setTurn(Integer turn) {
        this.turn = turn;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
}
