package com.adex.fluxbot.game;

import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.keeper.Keeper;

import java.util.ArrayList;

/**
 * A player in the game
 */
public class Player {

    public final long userId;

    private final ArrayList<Card> hand;
    private final ArrayList<Keeper> keepers;

    private int hiddenKeeperIndex;

    /**
     * Creates a new player for the game.
     * Requires that the rules and card pile are already defined.
     *
     * @param userId Discord user id of the player.
     * @param game   Game
     */
    public Player(long userId, Flux game) {
        this.userId = userId;
        hiddenKeeperIndex = -1;

        keepers = new ArrayList<>();

        int cardCount = Math.min(Flux.CARDS_IN_STARTING_HAND, game.ruleset.get(Rule.HAND_LIMIT));
        hand = new ArrayList<>();

        Card[] cards = game.cards.draw(new Card[cardCount], cardCount);
        if (cards == null) return;
        for (Card card : cards) {
            if (card == null) break;
            hand.add(card);
        }
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public ArrayList<Keeper> getKeepers() {
        return keepers;
    }

    public int getHiddenKeeperIndex() {
        return hiddenKeeperIndex;
    }
}
