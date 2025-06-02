package brrrr.go.horsey.orm;

import brrrr.go.horsey.rest.JENSerializer;
import brrrr.go.horsey.service.JEN;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    // allows for games up to 22x22 in size (22 * 22 + 7 = 491 < 512)
    @Column(length = 512)
    @Convert(converter = JENConverter.class)
    @JsonSerialize(using = JENSerializer.class)
    private JEN jen;

    @Column
    private Integer turnNumber;

    @JoinColumn(name = "game_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Game game;

    public UUID getId() {
        return id;
    }

    public Position setId(UUID id) {
        this.id = id;
        return this;
    }

    public JEN getJen() {
        return jen;
    }

    public Position setJen(JEN jen) {
        this.jen = jen;
        return this;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public Position setTurnNumber(Integer turnNumber) {
        this.turnNumber = turnNumber;
        return this;
    }

    public Game getGame() {
        return game;
    }

    public Position setGame(Game game) {
        this.game = game;
        return this;
    }
}
