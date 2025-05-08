package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.UUID;

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

    public Game getGame(String gameId) throws NotFoundException {
        try {
            return em.find(Game.class, gameId);
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }


    @Transactional
    public Game createGame(Game game) {
        em.persist(game);
        return game;
    }

    @Transactional
    public void deleteGame(String gameId) {
        Game game = em.find(Game.class, UUID.fromString(gameId));
        if (game == null) {
            throw new NotFoundException("Game not found");
        }
        em.remove(game);
    }

    /**
     * Suitably updates the given game.
     * Can update the game state and add a guest. May do more in the future.
     * @param gameId the id of the game to join the user to
     * @param guest the user to add to the game
     * @return the updated game
     */
    @Transactional
    public Game addGuest(String gameId, User guest) {
        Game existingGame = em.find(Game.class, UUID.fromString(gameId));
        User existingUser = em.find(User.class, guest.getUsername());
        if (existingUser == null) {
            throw new BadRequestException("User not found");
        }
        if (existingGame == null) {
            throw new NotFoundException("Game not found");
        }
        if (existingGame.addGuest(guest)){
            // If the game is updated to have a guest, we need to update the game state
            // to reflect that it is now in progress.
            existingGame.setState(Game.State.IN_PROGRESS);
        }
        em.persist(existingGame);
        return existingGame;
    }

    @Transactional
    public Game makeTurn(String gameId, Short turn, User user) {
        return null;
    }



}
