package com.adex.fluxbot.game.card;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data type providing basic methods for a pile.
 * <p>
 * Offers O(1) speed for adding or removing a card from either pile.
 *
 * @param <T> Class of the card
 */
public class Pile<T> {

    private final ArrayDeque<T> drawPile;
    private final ArrayDeque<T> discardPile;

    public Pile(List<T> elements) {
        Collections.shuffle(elements);
        drawPile = new ArrayDeque<>(elements);
        discardPile = new ArrayDeque<>(elements.size());
    }

    /**
     * Returns the card in top of the drawing pile and removes it from the pile.
     * Shuffles the discard pile into the drawing pile if necessary.
     * Returns null if there are no cards in either pile.
     */
    @Nullable
    public T draw() {
        if (drawPile.isEmpty()) {
            shuffle();
            if (drawPile.isEmpty()) return null;
        }
        return drawPile.removeFirst();
    }


    /**
     * Returns the specified amount of cards from the top of the drawing pile and removes them from the pile.
     * Shuffles the discard pile into the drawing pile if necessary.
     * Returns null if there are no cards in either pile.
     * If there are some, but not enough cards in the piles,
     * all the cards left will be added to the beginning of the array and the remaining values will be set to null.
     *
     * @param amount Amount of cards to draw.
     * @param array  Array for the cards.
     */
    @Nullable
    public T[] draw(T[] array, int amount) {
        if (array.length < amount) {
            throw new IllegalArgumentException("Cannot draw cards: array for cards is too short");
        }

        if (drawPile.size() < amount) {
            shuffle();
            if (drawPile.isEmpty()) return null;
        }

        int size = drawPile.size();
        for (int i = 0; i < amount; i++) {
            array[i] = i < size ? drawPile.removeFirst() : null;
        }

        return array;
    }

    /**
     * Adds the cards to the top of the discard pile.
     *
     * @param cards Cards
     */
    public void addToDiscardPile(T... cards) {
        for (T card : cards) {
            discardPile.addFirst(card);
        }
    }

    /**
     * Adds the cards to the top of the discard pile.
     *
     * @param cards Cards
     */
    public void addToDiscardPile(List<T> cards) {
        for (T card : cards) {
            discardPile.addFirst(card);
        }
    }

    /**
     * Returns the card in top of the discard pile and removes it from the pile.
     * Returns null if there are no cards in the discard pile.
     */
    @Nullable
    public T drawFromDiscard() {
        if (discardPile.isEmpty()) return null;

        return discardPile.removeFirst();
    }

    /**
     * Returns the specified amount of cards from the top of the discard pile and removes them from the pile.
     * Returns null if there are no cards in the discard pile.
     * If there are some, but not enough cards in the pile,
     * all the cards left will be added to the beginning of the array and the remaining values will be set to null.
     *
     * @param amount Amount of cards to draw.
     * @param array  Array for the cards.
     */
    @Nullable
    public T[] drawFromDiscard(T[] array, int amount) {
        if (array.length < amount) {
            throw new IllegalArgumentException("Cannot draw cards: array for cards is too short");
        }

        if (discardPile.isEmpty()) return null;

        int size = discardPile.size();
        for (int i = 0; i < amount; i++) {
            array[i] = i < size ? discardPile.removeFirst() : null;
        }

        return array;
    }

    public void shuffle() {
        ArrayList<T> list = new ArrayList<>(discardPile);
        Collections.shuffle(list);
        drawPile.addAll(list);
        discardPile.clear();
    }

}
