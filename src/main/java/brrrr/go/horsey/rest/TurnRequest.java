package brrrr.go.horsey.rest;

import brrrr.go.horsey.orm.Player;

/**
 * Helper Class to receive the request to make a turn in a singular JSON object.
 *
 */
public class TurnRequest {
    private Byte column;
    private Player player;

    public TurnRequest() {
    }

    public Byte getColumn() {
        return column;
    }
    public void setColumn(Byte column) {
        this.column = column;
    }
    public Player getUser() {
        return player;
    }
    public void setUser(Player player) {
        this.player = player;
    }
}
