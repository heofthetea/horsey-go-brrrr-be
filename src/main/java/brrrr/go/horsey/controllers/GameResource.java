package brrrr.go.horsey.controllers;


import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.User;
import brrrr.go.horsey.rest.LoggingFilter;
import brrrr.go.horsey.rest.TurnRequest;
import brrrr.go.horsey.service.GameService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/games")
public class GameResource {

    @Inject
    GameService gameService;

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);

    @GET
    @Path("/")
    public List<Game> getGames(@QueryParam("user_id") String userId) {
        return gameService.getGamesByUser(userId);
    }

    @GET
    @Path("/{game_id}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Game found"),
            @APIResponse(responseCode = "404", description = "Game not found")
    })
    public Game getGame(@PathParam("game_id") String gameId) {
        return gameService.getGame(gameId);
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
    @Path("/join/{game_id}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Guest successfully added"),
            @APIResponse(responseCode = "404", description = "Game not found")
    })
    public Game updateGame(@PathParam("game_id") String gameId, @RequestBody User guest) {
        return gameService.addGuest(gameId, guest);
    }

    /**
     * Makes a turn in the game.
     * @param gameId the id of the game to make a turn in
     * @param turn Integer representing the column the turn was made in
     * @return
     */
    @PUT
    @Path("/{game_id}/make-turn")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Turn made successfully"),
            @APIResponse(responseCode = "404", description = "Game not found"),
            @APIResponse(responseCode = "403", description = "Not your turn donkey")
    })
    public Game makeTurn(@PathParam("game_id") String gameId, TurnRequest turn) {
        return gameService.makeTurn(gameId, turn.getColumn(), turn.getUser());
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



}
