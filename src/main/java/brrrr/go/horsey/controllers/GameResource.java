package brrrr.go.horsey.controllers;


import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.rest.LoggingFilter;
import brrrr.go.horsey.service.GameService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.jboss.logging.Logger;

import java.util.ArrayList;
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

    @POST
    @Path("/create")
    public Game createGame(Game game) {
        return gameService.createGame(game);
    }

}
