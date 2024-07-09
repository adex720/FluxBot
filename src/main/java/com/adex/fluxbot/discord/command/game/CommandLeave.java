package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Locale;

public class CommandLeave extends Command {

    public CommandLeave() {
        super("leave", "Leaves a game");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Leave game", "Cannot leave game", "You are not in a game"))
                    .setEphemeral(true).queue();
            return;
        }

        OptionMapping optionData = context.getOption("confirmation");
        if (!optionData.getAsString().toUpperCase(Locale.ROOT).equals("YES")) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot leave game", "Please confirm you want to leave the game", "Type YES as the confirmation option"))
                    .setEphemeral(true).queue();
            return;
        }

        FluxGame game = context.getGame();
        long userId = context.getUserId();
        if (game.currentPlayerUserId() == userId) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot leave game", "It's your turn", "Finish your turn before leaving"))
                    .setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(MessageCreator.createDefault("Leaving game")).queue();
        game.removePlayerFromGame(userId, false);
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.STRING, "confirmation", "Type YES to confirm you want to leave the game", true)};
    }
}
