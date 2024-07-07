package com.adex.fluxbot.game;

import com.adex.fluxbot.Util;
import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.card.Pile;
import com.adex.fluxbot.game.goal.Goal;
import com.adex.fluxbot.game.goal.Goals;
import com.adex.fluxbot.game.rule.Rule;
import com.adex.fluxbot.game.rule.Ruleset;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class FluxGame {

    public static final int CARDS_IN_STARTING_HAND = 3;

    private final Ruleset ruleset;

    private final ArrayList<Player> players;
    private int playerCount;

    public final Pile<Card> cards;

    private Goal goal;

    private int currentPlayerId;
    private int nextPlayerId;

    public final int gameId;

    // Turn info
    private TurnState turnState;
    private int cardsDrawn;
    private int cardsPlayed;

    private Card cardPlaying;


    // Discord channel info
    private final TextChannel channel;
    private final long channelId;
    private final long guildId;

    public FluxGame(long userId, int gameId, TextChannel channel) {
        this.gameId = gameId;
        this.channel = channel;
        this.channelId = channel.getIdLong();
        this.guildId = channel.getGuild().getIdLong();

        this.ruleset = new Ruleset();
        cards = new Pile<>(Card.getCards());

        goal = Goals.NO_GOAL;

        players = new ArrayList<>();
        players.add(new Player(userId, this));

        playerCount = 1;
        currentPlayerId = 0;
        nextPlayerId = 1;

        turnState = TurnState.PAUSED;
        cardsDrawn = 0;
        cardsPlayed = 0;
        cardPlaying = null;
    }

    /**
     * Returns the current value of the rule.
     *
     * @param rule Rule
     */
    public int getRule(Rule rule) {
        return ruleset.get(rule);
    }

    /**
     * Returns the current value of the rule.
     *
     * @param ruleId Id of the rule
     */
    public int getRule(int ruleId) {
        return ruleset.get(ruleId);
    }

    /**
     * Sets the value of the rule into the given value.
     * Checks if the value is in the allowed range.
     *
     * @param rule  Rule
     * @param value New value
     * @return true if cards need to be discarded or a keeper to hide must be chosen.
     */
    public boolean setRule(Rule rule, int value) {
        ruleset.set(rule, value);

        boolean waitForUser = false;
        if (rule == Rule.HAND_LIMIT) {
            if (handLimitChanged()) waitForUser = true;
        } else if (rule == Rule.KEEPER_LIMIT) {
            if (keeperLimitChanged()) waitForUser = true;
        } else if (rule == Rule.KEEPERS_SECRET) {
            if (keepersSecretChanged()) waitForUser = true;
        }

        return waitForUser;
    }

    /**
     * Sets the value of the rule into the given value.
     * Checks if the value is in the allowed range.
     *
     * @param ruleId Id of the rule
     * @param value  New value
     * @return true if cards need to be discarded or a keeper to hide must be chosen.
     */
    public boolean setRule(int ruleId, int value) {
        ruleset.set(ruleId, value);

        boolean waitForUser = false;
        if (ruleId == Rule.HAND_LIMIT.id) {
            if (handLimitChanged()) waitForUser = true;
        } else if (ruleId == Rule.KEEPER_LIMIT.id) {
            if (keeperLimitChanged()) waitForUser = true;
        } else if (ruleId == Rule.KEEPERS_SECRET.id) {
            if (keepersSecretChanged()) waitForUser = true;
        }

        return waitForUser;
    }

    /**
     * Sets the value of the rule into its default value.
     *
     * @param rule Rule
     */
    public void resetRule(Rule rule) {
        ruleset.reset(rule);
        if (rule == Rule.KEEPERS_SECRET) keepersSecretChanged();
        else if (rule == Rule.DRAW_COUNT) drawCountChanged();
    }

    /**
     * Sets the value of the rule into its default value.
     *
     * @param ruleId Id of the rule
     */
    public void resetRule(int ruleId) {
        ruleset.reset(ruleId);
        if (ruleId == Rule.KEEPERS_SECRET.id) keepersSecretChanged();
        else if (ruleId == Rule.DRAW_COUNT.id) drawCountChanged();
    }

    /**
     * Sets the value of each rule into its default value.
     */
    public void resetAllRules() {
        ruleset.resetAll();
        keepersSecretChanged();
        drawCountChanged();
    }

    /**
     * Returns the player whose turn it is.
     */
    public Player currentPlayer() {
        return players.get(currentPlayerId);
    }

    /**
     * Returns the Discord user id of the player whose turn it is.
     */
    public long currentPlayerUserId() {
        return players.get(currentPlayerId).userId;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Adds a new player to the game.
     * The player will always be added so that their turn is before the player whose turn it is currently.
     *
     * @param userId Discord user id of the player
     */
    public void addPlayer(long userId) {
        for (Player player : players) {
            if (player.userId == userId) return; // player is already in the
        }

        if (currentPlayerId == 0) {
            players.add(new Player(userId, this));
        } else {
            players.add(currentPlayerId, new Player(userId, this));
        }

        playerCount++;
        currentPlayerId++;
        nextPlayerId++;
    }

    /**
     * Removes a player from the game.
     * Adds all of their keepers and hand cards to the discard pile.
     * If the player removed is the one whose turn it is, finishes the turn with default actions.
     *
     * @param userId Discord user id of the player.
     * @param kicked Was teh player kicked.
     */
    public void removePlayerFromGame(long userId, boolean kicked) {

        int playerIndex = 0;
        Player player = null;

        for (Player p : players) {
            if (p.userId == userId) {
                player = p;
                break;
            }
            playerIndex++;
        }

        if (player == null) return; // User is not in the game

        if (playerCount == 2) {
            handleForfeit(playerIndex == 1 ? 0 : 1);
            return;
        }

        if (playerIndex == currentPlayerId) {
            //TODO: play turn

            if (nextPlayerId == currentPlayerId) {
                // TODO: play second turn
            }
        }

        cards.addToDiscardPile(player.getKeepersAsCards());
        cards.addToDiscardPile(player.getHand());

        players.remove(playerIndex);
        if (playerIndex < currentPlayerId) {
            currentPlayerId--;
            if (nextPlayerId != 0) nextPlayerId--;
        }
        playerCount--;

        channel.sendMessage("<@" + userId + "> has " + (kicked ? "been kicked from" : "left") + " the game.").queue();
    }

    /**
     * Increases cards played this turn count.
     * Starts next turn if this was the last card to be played.
     * Should only be called if the card should count towards the play rule limit. (Played with /play)
     * Should always be called AFTER {@link Card#onPlay(FluxGame, EventContext)}
     */
    public void cardPlayed() {
        cardsPlayed++;
        if (cardsPlayed >= ruleset.get(Rule.PLAY_COUNT)) endTurn();
    }

    /**
     * Draws more cards for the current player if needed.
     * Should be called when the draw count rule is changed.
     */
    public void drawCountChanged() {
        int drawCount = getRule(Rule.DRAW_COUNT);
        if (cardsDrawn >= drawCount) return;

        Player player = currentPlayer();

        int cardsNeeded = drawCount - cardsDrawn;
        int cardsDrawn = player.addCardsToHand(cards, cardsNeeded);

        channel.sendMessageEmbeds(MessageCreator.createDefault("The draw rule was changed to " + drawCount,
                player.username + " drew " + Util.combineCountAndWord(cardsDrawn, "more card"),
                MessageCreator.COMMAND_TIPS)).queue();
    }

    /**
     * Checks hand limit from other players.
     * Should always be called when hand limit is changed.
     * Doesn't need to be called if the limit is removed.
     *
     * @return true if cards need to be discarded.
     */
    public boolean handLimitChanged() {
        return checkHandLimitFromOthers(currentPlayer());
    }

    /**
     * Checks keeper limit from other players.
     * Should always be called when keeper limit is changed.
     * Doesn't need to be called if the limit is removed.
     *
     * @return true if cards need to be discarded.
     */
    public boolean keeperLimitChanged() {
        return checkKeeperLimitFromOthers(currentPlayer());
    }

    /**
     * @return true if at least one player needs to select which card to hide.
     */
    public boolean keepersSecretChanged() {
        //TODO
        return false;
    }

    public TurnState getTurnState() {
        return turnState;
    }

    public boolean canPlayCard() {
        return turnState == TurnState.WAITING_CARD_TO_PLAY && cardPlaying == null;
    }

    /**
     * Updates current player id
     */
    public void prepareNextTurn() {
        currentPlayerId = nextPlayerId;
        nextPlayerId++;
        if (nextPlayerId >= playerCount) nextPlayerId = 0;
    }

    /**
     * Resets turn specific variables such as cards played count during the turn.
     * Draws cards for the player and sends message to Discord.
     */
    public void startTurn() {
        Player player = currentPlayer();
        int drawCount = ruleset.get(Rule.DRAW_COUNT);
        cardsDrawn = player.addCardsToHand(cards, drawCount);

        if (player.getHandSize() == 0) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle(player.username + "'s turn")
                    .setColor(0)
                    .addField("", player.getAsMention() + " has no cards in hand and there are none in the drawing pile.", true)
                    .build()).queue();

            endTurn();
            return;
        }

        cardsPlayed = 0;
        turnState = TurnState.WAITING_CARD_TO_PLAY;

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle(player.username + "'s turn")
                .setColor(0)
                .addField("", player.getAsMention() + " drew " + Util.combineCountAndWord(cardsDrawn, "card"), true)
                .build()).queue();
    }

    /**
     * Checks for hand and keeper limit.
     * If they aren't hit, starts next player's turn.
     * Should be called after player has played cards.
     */
    public void endTurn() {
        if (checkHandLimit(currentPlayer())) return;
        if (checkKeeperLimit(currentPlayer())) return;

        prepareNextTurn();
        startTurn();
    }

    /**
     * Checks if the player needs to discard cards due to hand limit.
     * Should be called at the end of the player's turn.
     * Sends a message and prepares for response if yes.
     *
     * @return true if cards need to be discarded, false if not.
     */
    public boolean checkHandLimit(Player player) {
        int handLimit = ruleset.get(Rule.HAND_LIMIT);
        if (handLimit < 0 || player.getHandSize() <= handLimit) return false; // No need to discard
        int discardCount = player.getHandSize() - handLimit;

        turnState = TurnState.WAITING_FOR_CARD_DISCARDING_CURRENT;
        channel.sendMessageEmbeds(MessageCreator.createDefault("Too many cards in hand",
                player.username + " needs to discard " + Util.combineCountAndWord(discardCount, "card"),
                "Use /discard to discard cards from your hand")).queue();
        return true;
    }

    /**
     * Checks if a player needs to discard cards due to hand limit.
     * Doesn't check the player whose turn it is.
     * Should be called when a new hand limit is played.
     *
     * @param current Player whose hands not to check. Can be null.
     * @return true if at least one player needs to discard hands, false if not.
     */
    public boolean checkHandLimitFromOthers(@Nullable Player current) {
        int handLimit = ruleset.get(Rule.HAND_LIMIT);
        if (handLimit == -1) return false;

        StringBuilder tooMany = new StringBuilder();
        for (Player player : players) {
            if (player == current) continue;
            if (player.getHandSize() > handLimit) {
                turnState = TurnState.WAITING_FOR_CARD_DISCARDING_OTHERS;
                if (!tooMany.isEmpty()) tooMany.append(", ");
                tooMany.append(player.getAsMention());
            }
        }

        if (tooMany.isEmpty()) return false;
        channel.sendMessageEmbeds(MessageCreator.createDefault("Hand limit is " + ruleset.get(Rule.HAND_LIMIT),
                "Use /discard to discard cards from your hand",
                tooMany + " have too many cards")).queue();

        return true;
    }

    /**
     * Checks if the player needs to discard keepers due to keeper limit.
     * Should be called at the end of the player's turn.
     * Sends a message and prepares for response if yes.
     *
     * @return true if keepers need to be discarded, false if not.
     */
    public boolean checkKeeperLimit(Player player) {
        int keeperLimit = ruleset.get(Rule.KEEPER_LIMIT);
        if (keeperLimit < 0 || player.getKeeperCount() <= keeperLimit) return false; // No need to discard
        int discardCount = player.getKeeperCount() - keeperLimit;

        turnState = TurnState.WAITING_FOR_KEEPER_DISCARDING_CURRENT;

        channel.sendMessageEmbeds(MessageCreator.createDefault("Too many keepers",
                player.username + " needs to discard " + Util.combineCountAndWord(discardCount, "keeper"),
                "Use /remove to remove keepers from in front of you")).queue();
        return true;
    }

    /**
     * Checks if a player needs to discard keepers due to keeper limit.
     * Doesn't check the player whose turn it is.
     * Should be called when a new keeper limit is played.
     *
     * @param current Player whose keepers not to check. Can be null.
     * @return true if at least one player needs to discard keepers, false if not.
     */
    public boolean checkKeeperLimitFromOthers(@Nullable Player current) {
        int keeperLimit = ruleset.get(Rule.KEEPER_LIMIT);
        if (keeperLimit == -1) return false;

        StringBuilder tooMany = new StringBuilder();
        for (Player player : players) {
            if (player == current) continue;
            if (player.getKeeperCount() > keeperLimit) {
                turnState = TurnState.WAITING_FOR_KEEPER_DISCARDING_OTHERS;
                if (!tooMany.isEmpty()) tooMany.append(", ");
                tooMany.append(player.getAsMention());
            }
        }

        if (tooMany.isEmpty()) return false;
        channel.sendMessageEmbeds(MessageCreator.createDefault("Keeper limit is " + ruleset.get(Rule.HAND_LIMIT),
                "Use /remove to remove keepers from in front of you",
                tooMany + " have too many keepers")).queue();

        return true;
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

    public void handleForfeit(int winnerId) {

    }

    public enum TurnState {
        WAITING_CARD_TO_PLAY, WAITING_FOR_CARD_CURRENT_PLAYER, WAITING_FOR_OTHERS, WAITING_FOR_CARD_DISCARDING_CURRENT,
        WAITING_FOR_KEEPER_DISCARDING_CURRENT, WAITING_FOR_CARD_DISCARDING_OTHERS, WAITING_FOR_KEEPER_DISCARDING_OTHERS,
        PAUSED
    }
}