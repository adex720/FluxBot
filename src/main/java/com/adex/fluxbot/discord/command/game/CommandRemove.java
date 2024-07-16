package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.keeper.Keeper;
import com.adex.fluxbot.game.rule.Rule;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandRemove extends Command {

    public CommandRemove() {
        super("remove", "Remove a keeper from in front of you");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Remove", "Cannot remove keeper", "You are not in a game"))
                    .setEphemeral(true).queue();
            return;
        }

        OptionMapping optionMapping = context.getOption("keeper");
        if (optionMapping == null) {
            event.replyEmbeds(MessageCreator.createDefault("Remove", "Cannot remove keeper",
                            "You must choose a keeper to be removed"))
                    .setEphemeral(true).queue();
            return;
        }

        Keeper keeper = Keeper.getKeeperById(optionMapping.getAsInt());
        if (keeper == null) {
            event.replyEmbeds(MessageCreator.createDefault("Remove", "Cannot remove keeper",
                            "You can only remove keepers you have in front of you"))
                    .setEphemeral(true).queue();
            return;
        }

        FluxGame game = context.getGame();
        Player player = game.currentPlayer();
        if (!player.getKeepers().contains(keeper)) {
            event.replyEmbeds(MessageCreator.createDefault("Remove", "Cannot remove keeper",
                            "You can only remove keepers you have in front of you"))
                    .setEphemeral(true).queue();
            return;
        }

        long userId = context.getUserId();
        // can current player remove keeper
        if (game.getTurnState() == FluxGame.TurnState.WAITING_FOR_KEEPER_DISCARDING_CURRENT && game.currentPlayerUserId() == userId) {
            game.removeKeeper(keeper, player, context);

            // can other players remove keepers
        } else if (game.getTurnState() == FluxGame.TurnState.WAITING_FOR_KEEPER_DISCARDING_OTHERS && game.currentPlayerUserId() != userId) {
            if (player.getHandSize() <= game.getRule(Rule.HAND_LIMIT, player)) {
                event.replyEmbeds(MessageCreator.createDefault("Remove", "Cannot remove keeper",
                                "You can only remove keepers when you have more than the keeper limit"))
                        .setEphemeral(true).queue();
                return;
            }
            game.removeKeeper(keeper, player, context);
        }
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "keeper", "Keeper to remove from in front of you", true, true)};
    }
}
