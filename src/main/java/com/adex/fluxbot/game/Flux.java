package com.adex.fluxbot.game;

import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.card.Pile;

import java.util.ArrayList;

public class Flux {

    private final Ruleset ruleset;

    private final ArrayList<Long> players;

    private final Pile<Card> cards;


    public Flux(long userId) {
        this.ruleset = new Ruleset();

        players = new ArrayList<>();
        players.add(userId);

        cards = new Pile<>(Card.getCards());
    }

    /**
     * Randomizes the order of the discard pile and puts the cards into the draw pile.
     * If there are still cards remaining in the draw pile,
     * their order will be unchanged and the shuffled cards will be added to the ArrayList before them.
     */
    public void shuffleDiscardPile() {
        cards.shuffle();
    }
}
