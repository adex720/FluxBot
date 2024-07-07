package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.card.Card;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CommandPlay extends Command {

    public CommandPlay() {
        super("play", "Plays a card in a game");
    }

    @Override
    public void execute(EventContext context) {
        MessageChannel channel = context.getChannel();
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Play", "Cannot play card", "You are not in a game"))
                    .setEphemeral(true).queue();
            return;
        }

        FluxGame game = context.getGame();
        if (game.currentPlayerUserId() != context.getUserId()) {
            event.replyEmbeds(MessageCreator.createDefault("Play", "Cannot play card", "You can only play cards during your own turn"))
                    .setEphemeral(true).queue();
            return;
        }

        if (game.getTurnState() != FluxGame.TurnState.WAITING_CARD_TO_PLAY) {
            event.replyEmbeds(MessageCreator.createDefault("Play", "Cannot play card", "You have other actions you must do first"))
                    .setEphemeral(true).queue();
            return;
        }

        OptionMapping optionMapping = context.getOption("card");
        if (optionMapping == null) {
            event.replyEmbeds(MessageCreator.createDefault("Play", "Cannot play card", "You must choose a card to be played"))
                    .setEphemeral(true).queue();
            return;
        }

        Card card = Card.getCardById(optionMapping.getAsInt());
        if (card == null) {
            event.replyEmbeds(MessageCreator.createDefault("Play", "Cannot play card", "That card doesn't exist in this game"))
                    .setEphemeral(true).queue();
            return;
        }

        Player player = game.currentPlayer();
        if (!player.getHand().contains(card)) {
            event.replyEmbeds(MessageCreator.createDefault("Play", "Cannot play card", "You can only play cards you have in your hand"))
                    .setEphemeral(true).queue();
            return;
        }

        card.onPlay(game, context);
        game.cardPlayed();
    }
}
