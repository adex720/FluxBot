package com.adex.fluxbot.game;

import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.card.Pile;
import com.adex.fluxbot.game.rule.Ruleset;

import java.util.ArrayList;

public class Flux {

    public static final int CARDS_IN_STARTING_HAND = 3;

    public final Ruleset ruleset;

    private final ArrayList<Player> players;

    public final Pile<Card> cards;


    public Flux(long userId) {
        this.ruleset = new Ruleset();
        cards = new Pile<>(Card.getCards());

        players = new ArrayList<>();
        players.add(new Player(userId, this));
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
}
