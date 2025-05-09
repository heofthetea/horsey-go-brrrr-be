package brrrr.go.horsey.orm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.util.Set;
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
    @Column(name = "game_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_time", nullable = false, insertable = false) // trigger default dbms-side
    @ColumnDefault("now()")
    private Timestamp startTime;

    @Column(name = "end_time")
    Timestamp endTime;

    @Column(name = "width", nullable = false)
    private Byte width;

    @Column(name = "height", nullable = false)
    private Byte height;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NOT_STARTED'")
    // default here is handled also by the constructor, because leaving it up to the dbms with insertable = false pretty much nuked every bit of logical behaviour JPA had remaining
    private State state;

    @Column(name = "to_move", nullable = false, insertable = false)
    @ColumnDefault("'HOST'") // nah bro I won't make the mistake of enumerating this BS again
    private String toMove;

    @ManyToOne
    @JoinColumn(name = "host", nullable = false)
    private Player host;

    @ManyToOne
    @JoinColumn(name = "guest")
    private Player guest;


    @JsonIgnore
    @OneToMany(mappedBy = "game", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Position> positions;

    public Game(Player host, Byte width, Byte height) {
        this.host = host;
        this.width = width;
        this.height = height;
        this.state = State.NOT_STARTED;
    }

    // default constructor is required by JPA
    public Game() {
        this.host = null;
        this.guest = null;
        this.width = 0;
        this.height = 0;
        this.startTime = new Timestamp(System.currentTimeMillis());
        this.state = State.NOT_STARTED;
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

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public Player getGuest() {
        return guest;
    }

    public void setGuest(Player guest) {
        this.guest = guest;
    }

    /**
     * Adds a guest to the game if the game does not have a guest yet, or the guest is null.
     * @param guest the guest to add
     * @return true if the guest was added, false otherwise
     */
    public boolean addGuest(Player guest) {
        if (guest == null)
            return false;
        if (this.guest != null)
            return false;
        this.guest = guest;
        return true;
    }

    public Byte getWidth() {
        return width;
    }

    public Game setWidth(Byte width) {
        this.width = width;
        return this;
    }

    public Byte getHeight() {
        return height;
    }

    public Game setHeight(Byte height) {
        this.height = height;
        return this;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Game setEndTime(Timestamp endTime) {
        this.endTime = endTime;
        return this;
    }

    public State getState() {
        return state;
    }

    public Game setState(State state) {
        this.state = state;
        return this;
    }

    public Game setState(String state) {
        this.state = State.valueOf(state);
        return this;
    }

    public enum State {
        NOT_STARTED("NOT_STARTED"),
        IN_PROGRESS("IN_PROGRESS"),
        HOST_WON("HOST_WON"),
        GUEST_WON("GUEST_WON"),
        DRAW("DRAW");

        final String value;

        private State(String value) {
            this.value = value;
        }

        public static State fromPlayerChar(char player) {;
            if (player == 'x') return HOST_WON;
            if (player == 'o') return GUEST_WON;
            return DRAW;
        }
    }

}