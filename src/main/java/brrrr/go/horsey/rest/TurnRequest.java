package brrrr.go.horsey.rest;

import brrrr.go.horsey.orm.User;

/**
 * Helper Class to receive the request to make a turn in a singular JSON object.
 *
 */
public class TurnRequest {
    private Byte column;
    private User user;

    public TurnRequest() {
    }

    public Byte getColumn() {
        return column;
    }
    public void setColumn(Byte column) {
        this.column = column;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
