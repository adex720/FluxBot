package com.adex.fluxbot.game;

import com.adex.fluxbot.game.card.Card;

import java.util.ArrayList;
import java.util.Collections;

public class Flux {

    private final Ruleset ruleset;

    private final ArrayList<Long> players;

    private final ArrayList<Card> drawPile;
    private final ArrayList<Card> discardPile;


    public Flux(long userId) {
        this.ruleset = new Ruleset();

        players = new ArrayList<>();
        players.add(userId);

        drawPile = new ArrayList<>();
        discardPile = new ArrayList<>();
    }

    /**
     * Randomizes the order of the discard pile and puts the cards into the draw pile.
     * If there are still cards remaining in the draw pile,
     * their order will be unchanged and the shuffled cards will be added to the ArrayList before them.
     */
    public void shuffleDiscardPile() {
        Collections.shuffle(discardPile);
        drawPile.addAll(drawPile.size(), discardPile);
        discardPile.clear();
    }
}
