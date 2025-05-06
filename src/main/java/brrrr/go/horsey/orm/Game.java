package brrrr.go.horsey.orm;

import brrrr.go.horsey.service.UserService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * ORM Model for a connect 4 game.
 * Required Attributes for creation:
 * - host
 * - dimensions (width, height)
 */
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_time", nullable = false, insertable = false) // trigger default dbms-side
    @ColumnDefault("now()")
    private Timestamp startTime;

    @Column(name = "end_time")
    Timestamp endTime;

    @Column(name = "width", nullable = false)
    private Short width;

    @Column(name = "height", nullable = false)
    private Short height;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NOT_STARTED'")
    // default here is handled also by the constructor, because leaving it up to the dbms with insertable = false pretty much nuked every bit of logical behaviour JPA had remaining
    private GameState state;

    @ManyToOne
    @JoinColumn(name = "host", nullable = false)
    private User host;

    @ManyToOne
    @JoinColumn(name = "guest")
    private User guest;

    public Game(User host, Short width, Short height) {
        this.host = host;
        this.width = width;
        this.height = height;
        this.state = GameState.NOT_STARTED;
    }

    // default constructor is required by JPA
    public Game() {
        this.host = null;
        this.guest = null;
        this.width = 0;
        this.height = 0;
        this.startTime = new Timestamp(System.currentTimeMillis());
        this.state = GameState.NOT_STARTED;
    }

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

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
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

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setState(String state) {
        this.state = GameState.valueOf(state);
    }

    public enum GameState {
        NOT_STARTED("NOT_STARTED"),
        IN_PROGRESS("IN_PROGRESS"),
        PLAYER_1_WON("HOST_WON"),
        PLAYER_2_WON("GUEST_WON"),
        DRAW("DRAW");

        final String value;

        private GameState(String value) {
            this.value = value;
        }
    }

}