package brrrr.go.horsey.orm;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "player") // cannot name a table 'user' in postgres
public class Player {

    @Id
    @Column(name = "username")
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    public Player(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // default constructor is required by JPA somehow
    public Player() {
        this.username = "";
        this.password = "";
        this.email = "";
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

