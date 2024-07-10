package com.adex.fluxbot.game;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class GameManager {

    private final HashMap<Integer, FluxGame> activeGames; // game id -> game
    private final HashMap<Long, Integer> playerGameIds; // user id -> game id

    private int nextId;

    public GameManager(int nextId) {
        activeGames = new HashMap<>();
        playerGameIds = new HashMap<>();
    }

    /**
     * Returns the game with the given id.
     * If no game with the id exists, returns null.
     */
    @Nullable
    public FluxGame getGameByGameId(int gameId) {
        return activeGames.get(gameId);
    }

    /**
     * Returns the player's current game.
     * If the player is not in a game, returns null.
     */
    @Nullable
    public FluxGame getGameByUserId(long userId) {
        return activeGames.get(playerGameIds.get(userId));
    }

    public FluxGame createGame(long userId, String hostUsername, TextChannel channel) {
        int gameId = nextId++;
        FluxGame game = new FluxGame(userId, hostUsername, gameId, channel);
        activeGames.put(gameId, game);
        playerGameIds.put(userId, gameId);
        return game;
    }

    /**
     * Adds a player to the game if they are not in a game yet.
     *
     * @param userId   Discord user id of the player
     * @param userName Discord username of the player
     * @param game     FluxGame game
     */
    public void addUserToGame(long userId, String userName, FluxGame game) {
        if (playerGameIds.containsKey(userId)) return;

        playerGameIds.put(userId, game.gameId);
        game.addPlayer(userId, userName);
    }

    public void removePlayerFromGame(long userId, FluxGame game, boolean kicked) {
        if (!playerGameIds.remove(userId, game.gameId)) return; // user is not in the game
        game.removePlayerFromGame(userId, kicked);
    }

    /**
     * Removes a game and user id connections to it.
     */
    public void removeGame(FluxGame game) {
        activeGames.remove(game.gameId);
        for (Player player : game.getPlayers()) {
            playerGameIds.remove(player.userId);
        }
    }


}
