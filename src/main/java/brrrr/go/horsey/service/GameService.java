package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.Position;
import brrrr.go.horsey.orm.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import net.bytebuddy.pool.TypePool;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GameService {
    @Inject
    EntityManager em;

    @Inject
    UserService userService;

    @Inject
    PositionService positionService;

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
            return em.find(Game.class, UUID.fromString(gameId));
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }


    @Transactional
    public Game createGame(Game game) {
        em.persist(game);
        // Create the default position for the game
        final JEN defaultJEN = new JEN(game.getWidth(), game.getHeight());
        Position defaultPosition = new Position()
                .setGame(game)
                .setTurnNumber(0)
                .setJen(defaultJEN);
        em.persist(defaultPosition);
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
     *
     * @param gameId the id of the game to join the user to
     * @param guest  the user to add to the game
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
        if (existingGame.addGuest(guest)) {
            // If the game is updated to have a guest, we need to update the game state
            // to reflect that it is now in progress.
            existingGame.setState(Game.State.IN_PROGRESS);
        }
        em.persist(existingGame);
        return existingGame;
    }

    /**
     * Make a turn in the game.
     * After making the turn, checks whether the game is over. If so, handles according logic.
     *
     * @param gameId the id of the game to make a turn in
     * @param turn   Integer representing the column the turn was made in
     * @param user   the user making the turn
     * @return
     */
    @Transactional
    public Position makeTurn(String gameId, Byte turn, User user) {
        Game game = em.find(Game.class, UUID.fromString(gameId));
        user = em.find(User.class, user.getUsername()); //todo might be optional need to verify
        if (game == null) {
            throw new NotFoundException("Game not found");
        }
        if (game.getState() != Game.State.IN_PROGRESS) {
            throw new ForbiddenException("Game is not in progress");
        }

        Position latest = positionService.getLatestPosition(game);

        // latest should never be null as default position gets created at creation
        JEN newJEN = latest.getJen().clone();

        if (!isAllowedToMove(game, newJEN, user)) {
            throw new ForbiddenException("Not your turn donkey");
        }

        try {
            newJEN.makeTurn(turn);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), 409); // there does not appear to be a "ConflictException" type
        }
        Position newPosition = new Position()
                .setJen(newJEN)
                .setGame(game)
                .setTurnNumber(latest.getTurnNumber() + 1);

        em.persist(newPosition);
        //TODO: send websocket message to clients


        // Check if the game is over
        if (newJEN.getState() != Game.State.IN_PROGRESS) {
            game.setState(newJEN.getState())
                    .setEndTime(Timestamp.valueOf(LocalDateTime.now()))
                    .setState(newJEN.getState());
            em.persist(game);
        }
        return newPosition;

    }


    private boolean isAllowedToMove(Game game, JEN jen, User user) {
        char userSymbol = '-';

        if (game.getHost().equals(user)) {
            userSymbol = 'x';
        }
        if (game.getGuest().equals(user)) {
            userSymbol = 'o';
        }

        if (userSymbol == '-') {
            throw new ForbiddenException("Not your game donkey");
        }
        return jen.getCurrentPlayer() == userSymbol;

    }


}
