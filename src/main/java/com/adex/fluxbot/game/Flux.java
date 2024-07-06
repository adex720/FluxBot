package com.adex.fluxbot.game;

import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.card.Pile;
import com.adex.fluxbot.game.goal.Goal;
import com.adex.fluxbot.game.goal.Goals;
import com.adex.fluxbot.game.rule.Ruleset;

import java.util.ArrayList;

public class Flux {

    public static final int CARDS_IN_STARTING_HAND = 3;

    public final Ruleset ruleset;

    private final ArrayList<Player> players;
    private int playerCount;

    public final Pile<Card> cards;

    private Goal goal;

    private int currentPlayerId;
    private int nextPlayerId;

    public Flux(long userId) {
        this.ruleset = new Ruleset();
        cards = new Pile<>(Card.getCards());

        goal = Goals.NO_GOAL;

        players = new ArrayList<>();
        players.add(new Player(userId, this));

        playerCount = 1;
        currentPlayerId = 0;
        nextPlayerId = 1;
    }

    /**
     * Returns the player whose turn it is.
     */
    public Player currentPlayer() {
        return players.get(currentPlayerId);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Randomizes the order of the discard pile and puts the cards into the draw pile.
     * If there are still cards remaining in the draw pile,
     * their order will be unchanged and the shuffled cards will be added to the ArrayList before them.
     */
    public void shuffleDiscardPile() {
        cards.shuffle();
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
        int winner = checkForWin();
        if (winner >= 0) handleWin(winner);
    }

    /**
     * Checks if a player wins the game.
     * If multiple players are winning, the one whose turn would be next wins.
     *
     * @return id of the player winning in the players ArrayList. -1 if no one is winning.
     */
    public int checkForWin() {
        // check if current player wins
        if (goal.check(players.get(currentPlayerId))) return currentPlayerId;

        // check players whose turn is still in the current round
        for (int id = currentPlayerId + 1; id < playerCount; id++) {
            if (goal.check(players.get(id))) return id;
        }

        // check players whose turn has already been played this round
        for (int id = 0; id < currentPlayerId; id++) {
            if (goal.check(players.get(id))) return id;
        }

        return -1; // Nobody is winning
    }

    public void handleWin(int winnerId) {

    }
}
