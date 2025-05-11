package brrrr.go.horsey.controllers;


import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.Player;
import brrrr.go.horsey.orm.Position;
import brrrr.go.horsey.rest.LoggingFilter;
import brrrr.go.horsey.rest.TurnRequest;
import brrrr.go.horsey.service.GameService;
import brrrr.go.horsey.service.PositionService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/games")
@Authenticated
public class GameResource {

    @Inject
    GameService gameService;

    @Inject
    PositionService positionService;

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);

    /**
     * Returns a list of games for a given user.
     * Games are sorted primarily by end time, secondarily by start time; descending.
     * This means that all unfinished games come first, then all finished games.
     */
    @GET
    @Path("/")
    public List<Game> getGames(@QueryParam("username") String username) {
        return gameService.getGamesByUser(username);
    }

    @GET
    @Path("/{game_id}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Game found"),
            @APIResponse(responseCode = "404", description = "Game not found")
    })
    public Game getGame(@PathParam("game_id") String gameId) {
        return gameService.getGameWithPosition(gameId);
    }

    @POST
    @Path("/create")
    public Game createGame(Game game) {
        return gameService.createGame(game);
    }

    /**
     * Joins a User to a game.
     */
    @PUT
    @Path("/{game_id}/join")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Guest successfully added"),
            @APIResponse(responseCode = "404", description = "Game not found")
    })
    public Game joinGame(@PathParam("game_id") String gameId, @RequestBody Player guest) {
        return gameService.addGuest(gameId, guest);
    }

    /**
     * Makes a turn in the game.
     * @param gameId the id of the game to make a turn in
     * @param turn Integer representing the column the turn was made in
     * @return the updated game
     */
    @PUT
    @Path("/{game_id}/make-turn")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Turn made successfully"),
            @APIResponse(responseCode = "404", description = "Game not found"),
            @APIResponse(responseCode = "403", description = "Not your turn donkey"),
            @APIResponse(responseCode = "409", description = "Turn impossible (out of bounds or invalid)")

    })
    public Game makeTurn(@PathParam("game_id") String gameId, TurnRequest turn) {
        return gameService.makeTurn(gameId, turn.getColumn(),  turn.getUser());
    }

    @DELETE
    @Path("/{game_id}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Game deleted successfully"),
            @APIResponse(responseCode = "404", description = "Game not found")
    })
    public void deleteGame(@PathParam("game_id") String gameId) {
        gameService.deleteGame(gameId);
    }

    @GET
    @Path("/{game_id}/history")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Latest turn found"),
            @APIResponse(responseCode = "404", description = "Game not found")
    })
    public List<Position> getGameHistory(@PathParam("game_id") String gameId) {
        return positionService.getGameHistory(gameService.getGameWithPosition(gameId));
    }



}
