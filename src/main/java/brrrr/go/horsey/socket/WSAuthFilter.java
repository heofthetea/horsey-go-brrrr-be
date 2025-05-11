package brrrr.go.horsey.socket;

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Singleton;


/**
 * Filter authentication headers into Websocket requests.
 * Usage client-side in javascript: {@code const ws = new WebSocket(url, ["access_token", bearerToken]);}
 * Source for this and {@link WebSocketSecurityConfigurator}: <a href="https://github.com/quarkusio/quarkus/issues/29919#issuecomment-1356917147">https://github.com/quarkusio/quarkus/issues/29919#issuecomment-1356917147</a>
 */
@Singleton
public class WSAuthFilter {
    @RouteFilter(401)
    void addAuthHeader(RoutingContext rc) {
        try {
            if (rc.request().headers().get("Sec-WebSocket-Protocol") != null) {
                String token = "Bearer " + rc.request().headers().get("Sec-WebSocket-Protocol").split(", ")[1];
                rc.request().headers().add("Authorization", token);
            }
        } finally {
            rc.next();
        }
    }
}