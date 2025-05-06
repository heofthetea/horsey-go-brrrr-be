package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class GameService {
    @Inject
    EntityManager em;

    @Inject
    UserService userService;

    public List<Game> getGamesByUser(String userId) throws NotFoundException {
        try {
            User user = userService.getUser(userId);
            return em.createQuery("SELECT g FROM Game g WHERE (host = :user  OR guest = :user)", Game.class)
                    .setParameter("user", user)
                    .getResultList();
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }


    @Transactional
    public Game createGame(Game game) {
        em.persist(game);
        return game;
    }

}
