package com.adex.fluxbot.game;

import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.card.Pile;
import com.adex.fluxbot.game.keeper.Keeper;
import com.adex.fluxbot.game.rule.Rule;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

/**
 * A player in the game
 */
public class Player {

    public final long userId;
    public final String username;

    private final ArrayList<Card> hand;
    private final ArrayList<Keeper> keepers;

    private int hiddenKeeperIndex;

    private final FluxGame game;

    /**
     * Creates a new player for the game.
     * Requires that the rules and card pile are already defined.
     *
     * @param userId   Discord user id of the player.
     * @param username Discord username of the player
     * @param game     Game
     */
    public Player(long userId, String username, FluxGame game) {
        this.game = game;

        this.username = username;
        this.userId = userId;

        hiddenKeeperIndex = -1;

        keepers = new ArrayList<>();

        int cardCount = Math.min(FluxGame.CARDS_IN_STARTING_HAND, game.getRule(Rule.HAND_LIMIT));
        hand = new ArrayList<>();

        Card[] cards = game.cards.draw(new Card[cardCount], cardCount);
        if (cards == null) return;
        for (Card card : cards) {
            if (card == null) break;
            hand.add(card);
        }
    }

    /**
     * Returns an {@link ArrayList<Card>} of the player's hand cards.
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    public int getHandSize() {
        return hand.size();
    }

    public int addCardsToHand(Pile<Card> cards, int amount) {
        return addCardsToHand(cards.draw(new Card[amount], amount));
    }

    public int addCardsToHand(@Nullable Card... cards) {
        if (cards == null) return 0;
        int cardsDrawn = 0;
        for (Card card : cards) {
            if (card == null) break;
            hand.add(card);
            cardsDrawn++;
        }
        if (cardsDrawn > 0) game.cardsDrawn();
        return cardsDrawn;
    }

    /**
     * Returns a random card from the player's hand.
     * Returns null if the player has no hand cards.
     *
     * @param random Random to use
     * @param remove Should the card be removed from the hand.
     */
    @Nullable
    public Card getRandomCard(Random random, boolean remove) {
        if (hand.isEmpty()) return null;

        int size = getHandSize();
        int index = random.nextInt(size);
        if (remove) return hand.remove(index);
        return hand.get(index);
    }

    public void removeCardFromHand(Card card) {
        hand.remove(card);
    }

    public void removeKeeper(Keeper keeper) {
        keepers.remove(keeper);
    }

    /**
     * Returns an {@link ArrayList<Keeper>} of the player's played keepers.
     */
    public ArrayList<Keeper> getKeepers() {
        return keepers;
    }


    /**
     * Returns an {@link ArrayList<Card>} of the player's played keepers as Cards.
     */
    public ArrayList<Card> getKeepersAsCards() {
        ArrayList<Card> list = new ArrayList<>(keepers.size());
        for (Keeper keeper : keepers) {
            list.add(keeper.getCard());
        }
        return list;
    }

    /**
     * Returns the amount of keepers the player has in from of them.
     */
    public int getKeeperCount() {
        return keepers.size();
    }

    public int getHiddenKeeperIndex() {
        return hiddenKeeperIndex;
    }

    public FluxGame getGame() {
        return game;
    }

    /**
     * Returns a Discord mention of the player
     *
     * @return <@userId>
     */
    public String getAsMention() {
        return "<@" + userId + ">";
    }
}
