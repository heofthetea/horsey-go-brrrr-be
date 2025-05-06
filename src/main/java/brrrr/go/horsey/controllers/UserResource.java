package brrrr.go.horsey.controllers;

import brrrr.go.horsey.orm.User;
import brrrr.go.horsey.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/users")
public class UserResource {
    @Inject
    UserService userService;

    @POST
    @Path("/register")
    @ResponseStatus(201)
    @Produces(MediaType.APPLICATION_JSON)
    public User registerUser(User user) {
        return userService.createUser(user);

    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseStatus(200)
    public User getUser(@QueryParam("id") String id, @QueryParam("username") String username) {
        if (id != null) {
            return userService.getUser(id);
        }
        if (username != null) {
            return userService.getUserByUsername(username);
        }
        throw new BadRequestException("Either 'id' or 'email' must be provided.");
    }
}
