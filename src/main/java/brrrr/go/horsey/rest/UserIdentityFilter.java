package brrrr.go.horsey.rest;

import brrrr.go.horsey.service.UserService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import io.quarkus.security.identity.SecurityIdentity;

import java.io.IOException;

@Provider
@Priority(2000)
public class UserIdentityFilter implements ContainerRequestFilter {

    @Inject
    SecurityIdentity identity;

    @Inject
    UserService userService;

    /**
     * Maps a JWT user to a user in the database.
     * @param requestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String username = identity.getPrincipal().getName();

        userService.getOrCreate(username);

//        // Optional: store current user in request context
//        requestContext.setProperty("currentUsername", username);
    }
}
