package brrrr.go.horsey.controllers;

import brrrr.go.horsey.orm.Player;
import brrrr.go.horsey.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/players")
public class UserResource {
    @Inject
    UserService userService;

    @POST
    @Path("/register")
    @ResponseStatus(201)
    @Produces(MediaType.APPLICATION_JSON)
    public Player registerUser(Player player) {
        return userService.createUser(player);

    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseStatus(200)
    public Player getUser(@PathParam("username") String username) {
        return userService.getUser(username);
    }
}
