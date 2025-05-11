package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Player;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UserService {

    @Inject
    EntityManager em;

    @Transactional
    public Player createUser(Player player) {
        em.persist(player);
        return player;
    }

    public Player getUser(String username) throws NotFoundException {
        try {
            return em.find(Player.class, username);
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }

    /**
     * Get or create a user.
     * Used to map users stored in keycloak to internal users in the database.
     * @param username
     * @return
     */
    public Player getOrCreate(String username) {
        try {
            return em.find(Player.class, username);
        } catch (NoResultException e) {
            Player player = new Player();
            player.setUsername(username);
            em.persist(player);
            return player;
        }
    }

}
