package brrrr.go.horsey.controllers;

import brrrr.go.horsey.orm.User;
import brrrr.go.horsey.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/user")
public class UserResource {
    @Inject
    UserService userService;

    @POST
    @Path("/register")
    public void registerUser(User user) {
        userService.createUser(user);

    }
}
