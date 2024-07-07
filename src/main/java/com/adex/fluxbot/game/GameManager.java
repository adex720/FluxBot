package com.adex.fluxbot.game;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class GameManager {

    private final HashMap<Integer, FluxGame> activeGames; // game id -> game
    private final HashMap<Long, Integer> playerGameIds; // user id -> game id

    public GameManager() {
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

    /**
     * Adds a player to the game if they are not in a game yet.
     *
     * @param userId Discord user id of the player
     * @param game   FluxGame game
     */
    public void addUserToGame(long userId, FluxGame game) {
        if (playerGameIds.containsKey(userId)) return;

        game.addPlayer(userId);
    }


}
