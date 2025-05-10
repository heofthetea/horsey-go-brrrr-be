package brrrr.go.horsey.socket;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;


import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/game/{gameId}")
@ApplicationScoped
public class GameWebSocket {

    Logger LOG = Logger.getLogger(GameWebSocket.class);

    private static final Map<UUID, Set<Session>> subscriptions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("gameId") String gameIdStr) {
        try {
            UUID gameId = UUID.fromString(gameIdStr);
            subscriptions.computeIfAbsent(gameId, k -> new CopyOnWriteArraySet<>()).add(session);
            LOG.debug("Session " + session.getId() + " subscribed to game " + gameId);
        } catch (IllegalArgumentException e) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid gameId: " + gameIdStr));
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

    public static void broadcastGameUpdate(UUID gameId, String json) {
        Set<Session> sessions = subscriptions.getOrDefault(gameId, Set.of());
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(json);
            }
        }
    }
}
