package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.Position;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class PositionService {

    @Inject
    EntityManager em;

    @Inject
    HorseyRedisClient redisClient;

    public Position getLatestPosition(Game g) {
        if (g == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }

        Position cached = redisClient.getLastCachedPosition(g);
        if (cached != null) {
            return cached;
        }

        return em
            .createQuery("SELECT p FROM Position p WHERE p.game = :game ORDER BY p.turnNumber DESC", Position.class)
            .setParameter("game", g)
            .setMaxResults(1)
            .getSingleResult();

        // Don't fill the cache here
    }

    public List<Position> getGameHistory(Game g) {
        if (g == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }

        // Probe for caches
        List<Position> cached = redisClient.getHistoryCache(g);
        if (cached != null) {
            return cached;
        }

        // Cache miss or error - fall back to database
        List<Position> history = em
            .createQuery("SELECT p FROM Position p WHERE p.game = :game ORDER BY p.turnNumber ASC", Position.class)
            .setParameter("game", g)
            .getResultList();

        redisClient.setHistoryCache(history, g);

        return history;
    }
}
