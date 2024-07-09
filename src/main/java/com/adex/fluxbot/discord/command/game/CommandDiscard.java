package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.card.Card;
import com.adex.fluxbot.game.rule.Rule;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandDiscard extends Command {

    public CommandDiscard() {
        super("discard", "Discard cards from your hand");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Discard", "Cannot discard card", "You are not in a game"))
                    .setEphemeral(true).queue();
            return;
        }

        OptionMapping optionMapping = context.getOption("card");
        if (optionMapping == null) {
            event.replyEmbeds(MessageCreator.createDefault("Discard", "Cannot discard card",
                            "You must choose a card to be discarded"))
                    .setEphemeral(true).queue();
            return;
        }

        Card card = Card.getCardById(optionMapping.getAsInt());
        if (card == null) {
            event.replyEmbeds(MessageCreator.createDefault("Discard", "Cannot discard card",
                            "You can only discard cards you have in your hand"))
                    .setEphemeral(true).queue();
            return;
        }

        FluxGame game = context.getGame();
        Player player = game.currentPlayer();
        if (!player.getHand().contains(card)) {
            event.replyEmbeds(MessageCreator.createDefault("Discard", "Cannot discard card",
                            "You can only discard cards you have in your hand"))
                    .setEphemeral(true).queue();
            return;
        }

        long userId = context.getUserId();
        // can current player discard
        if (game.getTurnState() == FluxGame.TurnState.WAITING_FOR_CARD_DISCARDING_CURRENT && game.currentPlayerUserId() == userId) {
            game.discardCard(card, player, context);

            // can other players discard
        } else if (game.getTurnState() == FluxGame.TurnState.WAITING_FOR_CARD_DISCARDING_OTHERS && game.currentPlayerUserId() != userId) {
            if (player.getHandSize() <= game.getRule(Rule.HAND_LIMIT)) {
                event.replyEmbeds(MessageCreator.createDefault("Discard", "Cannot discard card",
                                "You can only discard cards when you have more than the hand limit"))
                        .setEphemeral(true).queue();
                return;
            }
            game.discardCard(card, player, context);
        }
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "card", "Card to discard", true, true)};
    }
}
