package com.adex.fluxbot.game;

import com.adex.fluxbot.Util;
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

    public final Ruleset ruleset;

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
     * Draws cards for the player and sends message to Discord.
     */
    public void startTurn() {
        Player player = currentPlayer();
        int drawCount = ruleset.get(Rule.DRAW_COUNT);
        int cardsDrawn = player.addCardsToHand(cards.draw(new Card[drawCount], drawCount));

        if (player.getHandSize() == 0) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle(player.username + "'s turn")
                    .setColor(0)
                    .addField("", player.getAsMention() + " has no cards in hand and there are none in the drawing pile.", true)
                    .build()).queue();

            // No need to check hand limit
            if (checkKeeperLimit(player)) return;

            prepareNextTurn();
            startTurn();
            return;
        }

        turnState = TurnState.WAITING_CARD_TO_PLAY;

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle(player.username + "'s turn")
                .setColor(0)
                .addField("", player.getAsMention() + " drew " + Util.combineCountAndWord(cardsDrawn, "card"), true)
                .build()).queue();
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

        turnState = TurnState.WAITING_FOR_KEEPER_DISCARDING_CURRENT;
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

        for (Player player : players) {
            if (player == current) continue;
            if (player.getKeeperCount() > keeperLimit) {
                turnState = TurnState.WAITING_FOR_KEEPER_DISCARDING_OTHERS;
                return true;
            }
        }

        return false;
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
