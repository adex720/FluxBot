package com.adex.fluxbot.game;

import com.adex.fluxbot.Util;
import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.card.Pile;
import com.adex.fluxbot.game.keeper.Keeper;
import com.adex.fluxbot.game.rule.Rule;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

        int cardCount = FluxGame.CARDS_IN_STARTING_HAND;
        int handLimit = game.getRule(Rule.HAND_LIMIT);
        if (handLimit >= 0 && cardCount < handLimit) cardCount = handLimit;
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

    /**
     * Returns a String containing the hand.
     * Each card is on its own line and contains a dash, the card emote and the card name.
     */
    public String getHandFormatted() {
        if (hand.isEmpty()) return "You don't have any cards in your hand";

        StringBuilder stringBuilder = new StringBuilder();
        for (Card card : hand) {
            if (!stringBuilder.isEmpty()) stringBuilder.append('\n');
            stringBuilder.append("- ").append(card.getEmoteAndName());
        }
        return stringBuilder.toString();
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
     * Returns a String containing the keepers in front of the player.
     * Each keeper is on its own line and contains a dash, the card emote and the card name.
     * Shows hidden keepers.
     * Should be used when showing own keepers
     */
    public String getKeepersFormatted() {
        if (keepers.isEmpty()) return "You don't have any cards in your hand";

        StringBuilder stringBuilder = new StringBuilder();
        for (Keeper keeper : keepers) {
            if (!stringBuilder.isEmpty()) stringBuilder.append('\n');
            stringBuilder.append("- ").append(keeper.getEmoteAndName());
        }
        return stringBuilder.toString();
    }

    /**
     * Returns a String containing the keepers in front of the player.
     * Each keeper is on its own line and contains a dash, the card emote and the card name.
     * Hides hidden keepers, only showing amount of hidden keepers
     * Should be used when showing keepers of another player
     */
    public String getKeepersFormattedForOthers() {
        if (keepers.isEmpty()) return "<@" + userId + "> doesn't have any keepers";

        int hidden = game.getRule(Rule.KEEPERS_SECRET);
        if (hidden == -1) {
            return Util.combineCountAndWord(keepers.size(), "hidden keeper");
        }

        if (hidden == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Keeper keeper : keepers) {
                if (!stringBuilder.isEmpty()) stringBuilder.append('\n');
                stringBuilder.append("- ").append(keeper.getEmoteAndName());
            }
            return stringBuilder.toString();
        }

        int keeperCount = keepers.size();
        if (keeperCount == 1) return "1 hidden keeper";

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keeperCount; i++) {
            if (i == hiddenKeeperIndex) continue;
            if (!stringBuilder.isEmpty()) stringBuilder.append('\n');
            stringBuilder.append("- ").append(keepers.get(i).getEmoteAndName());
        }
        return stringBuilder.append("\n+ 1 hidden keeper").toString();
    }

    /**
     * Returns the amount of keepers the player has in from of them.
     */
    public int getKeeperCount() {
        return keepers.size();
    }

    public void hideKeeper(int index) {
        hiddenKeeperIndex = index;
    }

    public void revealKeepers() {
        hiddenKeeperIndex = -1;
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

    /**
     * Creates a {@link MessageEmbed.Field} with the user tag as title and contains amount of cards in the player's hand
     * and the keepers.
     * If the keepers are hidden, only shows the amount of them.
     */
    public MessageEmbed.Field getInfoField() {
        return new MessageEmbed.Field(username,
                Util.combineCountAndWord(getHandSize(), "card") + " in hand\n" + getHandFormatted(), true);
    }
}
