package brrrr.go.horsey.orm;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import javax.print.DocFlavor;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private Timestamp startTime;

    @Column
    private Short width;

    @Column
    private Short height;


    @Column
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NOT_STARTED'")
    private GameState state;

    @ManyToOne
    @JoinColumn(name = "player_1", nullable = false)
    private User player1;

    @ManyToOne
    @JoinColumn(name = "player_2", nullable = false)
    private User player2;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public Short getWidth() {
        return width;
    }
    public void setWidth(Short width) {
        this.width = width;
    }
    public Short getHeight() {
        return height;
    }
    public void setHeight(Short height) {
        this.height = height;
    }

    public enum GameState {
        NOT_STARTED("NOT_STARTED"),
        IN_PROGRESS("IN_PROGRESS"),
        PLAYER_1_WON("PLAYER_1_WON"),
        PLAYER_2_WON("PLAYER_2_WON"),
        DRAW("DRAW");

        final String value;

        private GameState(String value) {
            this.value = value;
        }
    }

}