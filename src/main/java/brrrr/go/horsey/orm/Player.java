package brrrr.go.horsey.orm;

import jakarta.persistence.*;

/**
 * ORM Model for a user/player.
 * The terminology is a bit mixed up sadly, all originating from the fact that it has to be called differently in the database.
 * Is acceptable only because I'm the only one working on this and eh, it works
 * Edit: This really only is a mapper entity to map users from keycloak to stuff in the main database
 */
@Entity
@Table(name = "player") // cannot name a table 'user' in postgres
public class Player {

    @Id
    @Column(name = "username")
    private String username;

    public Player(String username) {
        this.username = username;
    }

    // default constructor is required by JPA somehow
    public Player() {
        this.username = "";
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

