package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game;
import brrrr.go.horsey.orm.Player;
import brrrr.go.horsey.orm.Position;
import brrrr.go.horsey.socket.GameSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static brrrr.go.horsey.socket.SocketSerialization.serializeJoin;
import static brrrr.go.horsey.socket.SocketSerialization.serializeTurn;

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
            Player player = userService.getUser(userId);
            return em.createQuery("SELECT g FROM Game g WHERE (host = :user  OR guest = :user) ORDER BY (endTime, startTime) desc", Game.class)
                    .setParameter("user", player)
                    .getResultList()
                    .stream() // add transient value current position to each game
                    .map(game -> game.setCurrentPosition(positionService.getLatestPosition(game).getJen()))
                    .collect(Collectors.toList());
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }

    public Game getGameWithPosition(String gameId) throws NotFoundException {
        try {
            Game game = em.find(Game.class, UUID.fromString(gameId));
            return game.setCurrentPosition(positionService.getLatestPosition(game).getJen());
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
        return game.setCurrentPosition(defaultJEN);
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
    public Game addGuest(String gameId, Player guest) {
        Game existingGame = em.find(Game.class, UUID.fromString(gameId));
        Player existingPlayer = em.find(Player.class, guest.getUsername());
        if (existingPlayer == null) {
            throw new BadRequestException("User not found");
        }
        if (existingGame == null) {
            throw new NotFoundException("Game not found");
        }
        if(existingGame.getGuest() != null) {
            throw new ForbiddenException("Game already has a guest");
        }
        if (existingGame.addGuest(guest)) {
            // If the game is updated to have a guest, we need to update the game state
            // to reflect that it is now in progress.
            existingGame.setState(Game.State.IN_PROGRESS);
        }
        em.persist(existingGame);

        try {
            // this is getting out of hand I should have simply used a getter for the latest position thing
            GameSocket.broadcastGameUpdate(existingGame.getId(), serializeJoin(guest, existingGame.setCurrentPosition(positionService.getLatestPosition(existingGame).getJen())));
        } catch (JsonProcessingException e) {
        }
        return existingGame.setCurrentPosition(positionService.getLatestPosition(existingGame).getJen());
    }

    /**
     * Make a turn in the game.
     * After making the turn, checks whether the game is over. If so, handles according logic.
     *
     * @param gameId the id of the game to make a turn in
     * @param turn   Integer representing the column the turn was made in
     * @param player the user making the turn
     * @return
     */
    @Transactional
    public Game makeTurn(String gameId, Byte turn, Player player) {
        Game game = em.find(Game.class, UUID.fromString(gameId));
        player = em.find(Player.class, player.getUsername()); //todo might be optional need to verify
        if (game == null) {
            throw new NotFoundException("Game not found");
        }
        if (game.getState() != Game.State.IN_PROGRESS) {
            throw new ForbiddenException("Game is not in progress");
        }

        Position latest = positionService.getLatestPosition(game);

        // latest should never be null as default position gets created at creation
        JEN newJEN = latest.getJen().clone();

        if (!isAllowedToMove(game, newJEN, player)) {
            throw new ForbiddenException("Not your turn donkey");
        }

        try {
            newJEN.makeTurn(turn);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), 409); // there does not appear to be a "ConflictException" type
        }
        Position newPosition = new Position()
                .setJen(newJEN)
                .setGame(game.setCurrentPosition(newJEN)) // stateful programming is funny
                .setTurnNumber(latest.getTurnNumber() + 1);

        em.persist(newPosition);


        boolean gameOver = false;
        // Check if the game is over
        if (newJEN.getState() != Game.State.IN_PROGRESS) {
            game.setState(newJEN.getState())
                    .setEndTime(Timestamp.valueOf(LocalDateTime.now()))
                    .setState(newJEN.getState());
            em.persist(game);
            gameOver = true;
        }

        try {
            GameSocket.broadcastGameUpdate(game.getId(), serializeTurn(turn, player, game, gameOver));
        } catch (JsonProcessingException e) {
            // idk what can i do if that happens it really shouldn't tho because everything is checked like 20 times but you never know
            // either way it's fine client should just reload the page
        }
        return game;

    }

    public boolean isPlayerInGame(String username, String gameId) {
        Player player = em.find(Player.class, username);
        Game game = em.find(Game.class, UUID.fromString(gameId));
        return game != null && (player.equals(game.getHost()) || player.equals(game.getGuest()));
    }


    private boolean isAllowedToMove(Game game, JEN jen, Player player) {
        char userSymbol = '-';

        // dirty workaround to allow for a player to play against themselves. Would be a pain to code the frontend otherwise
        if (game.getHost().equals(game.getGuest())) {
            return true;
        }

        if (game.getHost().equals(player)) {
            userSymbol = 'x';
        }
        if (game.getGuest().equals(player)) {
            userSymbol = 'o';
        }

        if (userSymbol == '-') {
            throw new ForbiddenException("Not your game donkey");
        }
        return jen.getCurrentPlayer() == userSymbol;

    }


}
