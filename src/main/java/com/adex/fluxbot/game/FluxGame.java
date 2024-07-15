package com.adex.fluxbot.game;

import com.adex.fluxbot.Util;
import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.card.Pile;
import com.adex.fluxbot.game.goal.Goal;
import com.adex.fluxbot.game.goal.Goals;
import com.adex.fluxbot.game.keeper.Keeper;
import com.adex.fluxbot.game.rule.Rule;
import com.adex.fluxbot.game.rule.Ruleset;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FluxGame {

    public static final int CARDS_IN_STARTING_HAND = 3;

    private final FluxBot bot;

    private final Ruleset ruleset;
    private final GameSettings settings;

    private final ArrayList<Player> players;
    private int playerCount;

    public static final int MIN_PLAYER_COUNT = 2;
    public static final int MAX_PLAYER_COUNT = 8;

    private final Set<Long> invites;

    public final Pile<Card> cards;

    private Goal goal;

    private int currentPlayerId;
    private int nextPlayerId;

    public final int gameId;

    // Turn info
    private TurnState turnState;
    private int cardsDrawn;
    private int cardsPlayed;

    // Should be null always when more specific information about playing the card isn't waited
    private Card cardPlaying;


    // Discord channel info
    private final MessageChannel channel;
    private final long channelId;
    private final long guildId;

    public FluxGame(FluxBot bot, long userId, String hostUsername, int gameId, TextChannel channel) {
        this.bot = bot;
        this.gameId = gameId;
        this.channel = channel;
        this.channelId = channel.getIdLong();
        this.guildId = channel.getGuild().getIdLong();

        this.settings = new GameSettings(0);

        this.ruleset = new Ruleset();
        cards = new Pile<>(Card.getCards());

        goal = Goals.NO_GOAL;

        players = new ArrayList<>();
        players.add(new Player(userId, hostUsername, this));
        invites = new HashSet<>(); // Invites do not expire but they can be cleared when the game starts // TODO: implement that

        playerCount = 1;
        currentPlayerId = 0;
        nextPlayerId = 1;

        turnState = TurnState.NOT_STARTED;
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
     * Returns a string containing active rules.
     * Each rule is shown on a different line.
     * Uses {@link Rule#display(int)} to get display format
     */
    public String getRules() {
        StringBuilder sb = new StringBuilder();

        for (Rule rule : Rule.values()) {
            String ruleString = rule.display(getRule(rule));
            if (ruleString.isEmpty()) continue;
            if (!sb.isEmpty()) sb.append('\n');
            sb.append(ruleString);
        }

        return sb.toString();
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
     * Returns the index of the player whose turn it is in the player list.
     */
    public int currentPlayerId() {
        return currentPlayerId;
    }

    public long getHostUserId() {
        return players.get(0).userId;
    }

    public String getHostAsMention() {
        return "<@" + getHostUserId() + ">";
    }

    /**
     * Returns the Discord user id of the player whose turn it is.
     */
    public long currentPlayerUserId() {
        return players.get(currentPlayerId).userId;
    }

    public boolean isUserInGame(long userId) {
        for (Player player : players) {
            if (player.userId == userId) return true;
        }
        return false;
    }

    public Player getPlayerByUserId(long userId) {
        for (Player player : players) {
            if (player.userId == userId) return player;
        }
        return null;
    }

    /**
     * Returns the player with the player id.
     * The host is the player with id 0.
     * The player whose turn if after the host has an id of 1, the next 2, and so on.
     */
    public Player getPlayerByPlayerId(int id) {
        return players.get(id);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getPlayersAsMentions() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Player player : players) {
            if (!stringBuilder.isEmpty()) stringBuilder.append(", ");
            stringBuilder.append(player.getAsMention());
        }
        return stringBuilder.toString();
    }

    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Adds a new player to the game.
     * The player will always be added so that their turn is before the player whose turn it is currently.
     *
     * @param userId Discord user id of the player
     */
    public void addPlayer(long userId, String username) {
        if (isUserInGame(userId)) return;

        if (currentPlayerId == 0) {
            players.add(new Player(userId, username, this));
            sendJoinMessage(userId, username, playerCount, turnState != TurnState.NOT_STARTED);
        } else {
            players.add(currentPlayerId, new Player(userId, username, this));
            sendJoinMessage(userId, username, currentPlayerId, turnState != TurnState.NOT_STARTED);

            currentPlayerId++;
            nextPlayerId++;
        }

        invites.remove(userId);
        playerCount++;
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

        if (turnState == TurnState.NOT_STARTED) {

            // Leaving player is the only player
            if (playerCount == 1) {
                channel.sendMessageEmbeds(MessageCreator.createDefault("You were the only player in the game so the game has been deleted.")).queue();
                delete();
                return;
            }

            // Removing player and sending message
            players.remove(0);
            playerCount--;
            channel.sendMessageEmbeds(MessageCreator.createDefault("Flux game", "A player has left te game", "<@" + userId + "> has left the game")).queue();

            // Leaving player was the host
            if (playerIndex == 0) {
                channel.sendMessageEmbeds(MessageCreator.createDefault("Flux game", "The host has left the game", players.get(0).getAsMention() + " is the new host")).queue();
                return;
                // Host is the first player in the players list so removing the old player already made someone the new host
            }

            return;
        }

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

    public void sendJoinMessage(long userId, String username, int playerId, boolean gameStarted) {
        if (gameStarted) {
            String previousPlayerMention = players.get(playerId - 1).getAsMention();
            String nextPlayerMention = currentPlayer().getAsMention();
            channel.sendMessageEmbeds(MessageCreator.createDefault("New player joined",
                    username + " has joined the game",
                    "<@" + userId + "> will play after " + previousPlayerMention + " and before " + nextPlayerMention)).queue();
            return;
        }
        channel.sendMessageEmbeds(MessageCreator.createDefault("New player joined",
                username + " has joined the game",
                "There are now " + playerCount + " players. Start the game with /start")).queue();
    }

    public boolean canInvite(long userId) {
        if (userId == getHostUserId()) return true;

        return settings.get(GameSettings.ANYONE_CAN_INVITE) == 1;
    }

    public void invite(long userId) {
        invites.add(userId);
    }

    public boolean isInvited(long userId) {
        return invites.contains(userId);
    }

    public boolean isInviteNeeded() {
        if (turnState == TurnState.NOT_STARTED) return settings.get(GameSettings.INVITE_ONLY) == 1;
        return settings.get(GameSettings.ANYONE_CAN_JOIN_WHEN_ON) == 0;
    }

    public boolean isFull() {
        return playerCount >= MAX_PLAYER_COUNT;
    }

    /**
     * Plays a random card from the current player's hand.
     */
    public void playRandomCardFromHand(EventContext context) {
        Player player = currentPlayer();
        if (player.getHandSize() == 0) return;
        Card card = player.getRandomCard(context.getRandom(), true);
        if (card == null) return;

        card.onPlay(this, context);
        cardPlayed(context);
    }

    /**
     * Increases cards played this turn count.
     * Starts next turn if this was the last card to be played.
     * Should only be called if the card should count towards the play rule limit. (Played with /play)
     * Should always be called AFTER {@link Card#onPlay(FluxGame, EventContext)}
     *
     * @param context Event context
     */
    public void cardPlayed(EventContext context) {
        cardsPlayed++;
        prepareCardPlaying(context);
    }

    /**
     * Checks if someone wins.
     * Should be called always when the keepers of any player change.
     */
    public void keepersChanged() {
        int winnerId = checkForWin();
        if (winnerId >= 0) handleWin(winnerId);
    }

    /**
     * Checks if someone wins.
     * Should be called always when the amount of cards in a player's hand increase.
     */
    public void cardsDrawn() {
        int winnerId = checkForWin();
        if (winnerId >= 0) handleWin(winnerId);
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

    public boolean hasStarted() {
        return turnState != TurnState.NOT_STARTED;
    }

    public void startGame(EventContext context) {
        currentPlayerId = 0;
        nextPlayerId = 1;

        channel.sendMessageEmbeds(MessageCreator.createDefault("Game starting",
                new MessageEmbed.Field("Turn order:", getPlayersAsMentions(), true),
                new MessageEmbed.Field("Tip", MessageCreator.COMMAND_TIPS, true))).queue();

        startTurn(context);
    }

    /**
     * Updates current player id
     */
    public void prepareNextTurn() {
        currentPlayerId = nextPlayerId;
        nextPlayerId++;
        cardsPlayed = 0;
        if (nextPlayerId >= playerCount) nextPlayerId = 0;
    }

    /**
     * Resets turn specific variables such as cards played count during the turn.
     * Draws cards for the player and sends message to Discord.
     */
    public void startTurn(EventContext context) {
        Player player = currentPlayer();
        int drawCount = ruleset.get(Rule.DRAW_COUNT);
        cardsDrawn = player.addCardsToHand(cards, drawCount);

        if (player.getHandSize() == 0) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle(player.username + "'s turn")
                    .setColor(0)
                    .addField("", player.getAsMention() + " has no cards in hand and there are none in the drawing pile.", true)
                    .build()).queue();

            endTurn(context);
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
     * Gets ready to receive new played card or pick it randomly if it's the last one, and it should be.
     * Starts next turn if no more cards can be played.
     */
    public void prepareCardPlaying(EventContext context) {
        if (cardsPlayed >= getRule(Rule.PLAY_COUNT)) endTurn(context);
        else if (getRule(Rule.FINAL_CARD_RANDOM) == 1 && cardsPlayed + 1 == getRule(Rule.PLAY_COUNT))
            playRandomCardFromHand(context);
    }

    /**
     * Checks for hand and keeper limit.
     * If they aren't hit, starts next player's turn.
     * Should be called after player has played cards.
     */
    public void endTurn(EventContext context) {
        if (checkHandLimit(currentPlayer())) return;
        handLimitIsMet(context);
    }

    /**
     * Should be called after hand limit is met when checked from any players.
     * Proceeds the game.
     */
    private void handLimitIsMet(EventContext context) {
        if (turnState == TurnState.WAITING_FOR_CARD_DISCARDING_OTHERS) { // Current player is discarding
            prepareCardPlaying(context);
        } else {
            if (checkKeeperLimit(currentPlayer())) return;
            keeperLimitIsMet(context);
        }
    }

    /**
     * Should be called after keeper limit is met when checked from any players.
     * Proceeds to the game.
     */
    private void keeperLimitIsMet(EventContext context) {
        if (turnState == TurnState.WAITING_FOR_KEEPER_DISCARDING_OTHERS) { // Current player is removing
            prepareCardPlaying(context);
        } else {
            prepareNextTurn();
            startTurn(context);
        }
    }

    private void handLimitIsMetForOthers(EventContext context) {
        Player player = currentPlayer();
        if (cardsPlayed >= getRule(Rule.PLAY_COUNT) || player.getHandSize() == 0) {
            endTurn(context);
            return;
        }

        turnState = TurnState.WAITING_CARD_TO_PLAY;
    }

    private void keeperLimitIsMetForOthers() {

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
     * Discards the card from the player's hand.
     * Adds the card to the discard pile.
     * Checks if more cards need to be discarded.
     * Continues the game if not.
     *
     * @param card    Card that was discarded.
     * @param player  Player discarding the card
     * @param context Event context to reply to
     */
    public void discardCard(Card card, Player player, EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        player.removeCardFromHand(card);
        event.replyEmbeds(MessageCreator.createDefault("Card discarding", card.type,
                player.username + "discarded card:",
                card.getEmoteAndName())).queue();

        cards.addToDiscardPile(card);

        if (turnState == TurnState.WAITING_FOR_CARD_DISCARDING_CURRENT) { // Current player is discarding
            if (player.getHandSize() <= getRule(Rule.HAND_LIMIT)) {
                handLimitIsMet(context);
            }
        } else { // Other than the current player is discarding
            boolean extraCards = false;
            for (Player checking : players) {
                // Current player may have extra cards when a new rule is played.
                if (checking.userId == player.userId) continue;

                if (checking.getHandSize() > getRule(Rule.HAND_LIMIT)) {
                    extraCards = true;
                    break;
                }
            }

            if (!extraCards) {
                handLimitIsMetForOthers(context);
            }
        }
    }

    /**
     * Removes the keeper from in front of the player.
     * Adds the card to the discard pile.
     * Checks if more keepers need to be removed.
     * Continues the game if not.
     *
     * @param keeper  Keeper that was removed.
     * @param player  Player discarding the card
     * @param context Event context
     */
    public void removeKeeper(Keeper keeper, Player player, EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        player.removeKeeper(keeper);
        event.replyEmbeds(MessageCreator.createDefault("Removing keeper", Card.Type.KEEPER,
                player.username + "discarded keeper:",
                keeper.getEmoteAndName())).queue();

        cards.addToDiscardPile(keeper.getCard());

        if (turnState == TurnState.WAITING_FOR_CARD_DISCARDING_CURRENT) { // Current player is discarding
            if (player.getKeeperCount() <= getRule(Rule.KEEPER_LIMIT)) {
                keeperLimitIsMet(context);
            }
        } else { // Other than the current player is discarding
            boolean extraCards = false;
            for (Player checking : players) {
                // Current player may have extra cards when a new rule is played.
                if (checking.userId == player.userId) continue;

                if (checking.getHandSize() > getRule(Rule.HAND_LIMIT)) {
                    extraCards = true;
                    break;
                }
            }

            if (!extraCards) {
                keeperLimitIsMet(context);
            }
        }
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
        delete();
    }

    public void handleForfeit(int winnerId) {
        delete();
    }

    /**
     * Removes the game from the {@link GameManager}
     */
    public void delete() {
        bot.getGameManager().removeGame(this);
    }

    public enum TurnState {
        WAITING_CARD_TO_PLAY, WAITING_FOR_CARD_CURRENT_PLAYER, WAITING_FOR_OTHERS, WAITING_FOR_CARD_DISCARDING_CURRENT,
        WAITING_FOR_KEEPER_DISCARDING_CURRENT, WAITING_FOR_CARD_DISCARDING_OTHERS, WAITING_FOR_KEEPER_DISCARDING_OTHERS,
        NOT_STARTED
    }
}
