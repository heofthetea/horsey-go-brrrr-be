package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.Position;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import redis.clients.jedis.RedisClient;

@ApplicationScoped
public class HorseyRedisClient {

    @ConfigProperty(name = "horsey.redis.address")
    String redisAddress;

    @Inject
    ObjectMapper objectMapper;

    private RedisClient rclient;
    private static final Logger LOG = Logger.getLogger(HorseyRedisClient.class);

    /**
     * Start the redis client after dependency injection happened.
     */
    @PostConstruct
    void initialize() {
        rclient = RedisClient.create(redisAddress);
    }

    /**
     * Creates a new redis cache for a game history.
     * On Failure: Logs a warning and fails silently.
     */
    public void setHistoryCache(List<Position> history, Game game) {
        try {
            // Serialize each element on its own, so we can transfer the list structure to Redis
            String[] serializedHistory = new String[history.size()];
            for (int i = 0; i < history.size(); i++) {
                serializedHistory[i] = objectMapper.writeValueAsString(history.get(i));
            }

            String key = String.format("game:%s:history", game.getId().toString());
            // Pushes the entire list to `key`, in a form where we can later append to it
            rclient.rpush(key, serializedHistory);
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize history for cache, bypassing cache", e);
        } catch (Exception e) {
            LOG.warn("Failed to write history to Redis cache", e);
        }
    }

    /**
     * Retrieve the last position of game from the cache.
     * Errors are treated as cache miss.
     *
     * @param game The game to probe the history for
     * @return The latest position on cache hit, null for cache miss
     */
    public Position getLastCachedPosition(Game game) {
        try {
            String key = String.format("game:%s:history", game.getId().toString());
            if (!rclient.exists(key)) {
                return null;
            }
            // lrange -1 -1 gets the last element of the list (which, since we rpush, is what we want)
            Position position = objectMapper.readValue(rclient.lrange(key, -1, -1).get(0), Position.class);
            return position;
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to deserialize position, treating as cache miss");
            return null;
        }
    }

    /**
     * Retrieve the history of game from the Redis Cache.
     * Errors are treated as a cache miss.
     *
     * @param game The game to retrieve history for
     * @return List of positions or null if error/cache miss
     */
    public List<Position> getHistoryCache(Game game) {
        try {
            // Retrieve the entire list from cache
            String key = String.format("game:%s:history", game.getId().toString());
            if (!rclient.exists(key)) {
                return null;
            }

            List<String> cached = rclient.lrange(key, 0, -1);

            // convert from JSON objects to DTOs
            List<Position> positions = new java.util.ArrayList<>();
            for (String json : cached) {
                positions.add(objectMapper.readValue(json, Position.class));
            }

            return positions;
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to deserialize cached history, treating as cache miss", e);
            return null;
        }
    }

    /**
     * Append a position to the game history cache.
     * If pushing fails, the entry is deleted to avoid invalid caching state.
     */
    public void appendPositionToHistoryCache(Game game, Position position) {
        try {
            String key = String.format("game:%s:history", game.getId().toString());
            rclient.rpush(key, objectMapper.writeValueAsString(position));
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize position for cache, clearing key", e);
        }
    }

    /**
     * Invalidate the History of game by deleting its key.
     * @param game the game whose history to invalidate
     */
    public void invalidateHistory(Game game) {
        String key = String.format("game:%s:history", game.getId().toString());
        rclient.del(key);
    }
}
