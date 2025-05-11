package brrrr.go.horsey.socket;


import brrrr.go.horsey.service.GameService;
import brrrr.go.horsey.service.UserService;
import io.quarkus.oidc.UserInfo;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.ForbiddenException;
import org.jboss.logging.Logger;


import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/ws/game/{gameId}", configurator = WebSocketSecurityConfigurator.class)
@ApplicationScoped
@Authenticated
public class GameSocket {

    Logger LOG = Logger.getLogger(GameSocket.class);
    @Inject
    UserInfo userInfo;

    @Inject
    GameService gameService;


    private static final Map<UUID, Set<Session>> subscriptions = new ConcurrentHashMap<>();

    @OnOpen
    @ActivateRequestContext
    public void onOpen(Session session, @PathParam("gameId") String gameIdStr) {
        try {
            UUID gameId = UUID.fromString(gameIdStr);
            if(!gameService.isPlayerInGame(userInfo.getPreferredUserName(), gameIdStr)) {
                throw new ForbiddenException("You are not a player in this game");
            }
            subscriptions.computeIfAbsent(gameId, k -> new CopyOnWriteArraySet<>()).add(session);
            LOG.debug("Session " + session.getId() + " subscribed to game " + gameId);
        } catch (IllegalArgumentException e) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid gameId: " + gameIdStr));
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
        } catch (ForbiddenException e) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "You are not a player in this game"));
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("gameId") String gameIdStr) {
        UUID gameId = UUID.fromString(gameIdStr);
        Set<Session> sessions = subscriptions.get(gameId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                subscriptions.remove(gameId);
            }
        }
        LOG.debug("Session " + session.getId() + " left game " + gameId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error for session " + session.getId());
        LOG.error(throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // WS is not intended to receive messages, should be done through the API
        LOG.debug("Received from client: " + message);
    }


    public static void broadcastGameUpdate(UUID gameId, String json) { // I'm not bothering figuring out how to cleanly send an object clearly it doesn't want me to

        Set<Session> sessions = subscriptions.getOrDefault(gameId, Set.of());
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(json, result -> {
                    if (result.getException() != null) {
                        Logger.getLogger(GameSocket.class).info("Error sending message to session " + session.getId() + ": " + result.getException().getMessage());
                    }
                });
            }
        }
    }
}
