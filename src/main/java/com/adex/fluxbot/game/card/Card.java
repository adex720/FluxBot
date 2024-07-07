package com.adex.fluxbot.game.card;

import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An instance of this class is a card which can be played.
 */
public abstract class Card {

    private static final ArrayList<Card> CARDS = new ArrayList<>();
    private static int CARD_AMOUNT = 0;

    public final String name;
    public final Type type;
    public final int id;

    public Card(String name, Type type) {
        this.name = name;
        this.type = type;
        this.id = CARD_AMOUNT++;

        CARDS.add(this);
    }

    public static int getCardAmount() {
        return CARD_AMOUNT;
    }

    public abstract void onPlay(FluxGame game, EventContext context);

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

    public static Card getCardById(int id) {
        return CARDS.get(id);
    }
}
