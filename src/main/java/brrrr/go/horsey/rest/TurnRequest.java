package brrrr.go.horsey.rest;

import brrrr.go.horsey.orm.User;

/**
 * Helper Class to receive the request to make a turn in a singular JSON object.
 *
 */
public class TurnRequest {
    private Short column;
    private User user;

    public TurnRequest() {
    }
    public TurnRequest(Short column, User user) {
        this.column = column;
        this.user = user;
    }

    public Short getColumn() {
        return column;
    }
    public void setColumn(Short column) {
        this.column = column;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
