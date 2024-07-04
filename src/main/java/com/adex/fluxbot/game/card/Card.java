package com.adex.fluxbot.game.card;

import com.adex.fluxbot.game.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An instance of this class is a card which can be played.
 */
public abstract class Card {

    private static final Set<Card> CARDS = new HashSet<>();

    public Card(String name, Type type) {
        this.name = name;
        this.type = type;

        CARDS.add(this);
    }

    public final String name;
    public final Type type;

    public abstract void onPlay(Flux game);

    public enum Type {
        RULE(0xe0c810), ACTION(0x0718ad), KEEPER(0x19d108), GOAL(0xe310ca);

        Type(int color) {
            this.color = color;
        }

        public final int color;
    }

    /**
     * Creates an ArrayList of the cards in a randomized order.
     *
     * @return Shuffled {@link ArrayList<Card>}
     */
    public static ArrayList<Card> getCards() {
        ArrayList<Card> cards = new ArrayList<>(CARDS);
        Collections.shuffle(cards);
        return cards;
    }
}
