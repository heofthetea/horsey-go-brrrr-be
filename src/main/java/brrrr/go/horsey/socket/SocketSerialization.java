package brrrr.go.horsey.socket;

import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

/**
 * I know I should probably be using like custom classes with annotations or something
 * but this is good enough lol (I'm not willing to learn)
 */
@ApplicationScoped
public class SocketSerialization {
    public static String serializeTurn(Byte turn, Player player, Game game, boolean gameOver) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(Map.of(
                "type", gameOver ? "GAME_OVER" : "GAME_UPDATED",
                "turnIn", turn,
                "turnBy", player.getUsername(),
                "game", game
        ));
    }

    public static String serializeJoin(Player player, Game game) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(Map.of(
                "type", "GUEST_JOINED",
                "player", player,
                "game", game
        ));
    }
}
