package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandKeepers extends Command {

    public CommandKeepers() {
        super("keepers", "View your keepers");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Keepers", "Cannot view your keepers", "You are not in a game"))
                    .setEphemeral(true).queue();
            return;
        }

        FluxGame game = context.getGame();
        if (!game.hasStarted()) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot view your keepers", "The game has not started", "Star the game with /start"))
                    .setEphemeral(true).queue();
            return;
        }

        Player player = game.getPlayerByUserId(context.getUserId());
        event.replyEmbeds(MessageCreator.createDefault("Keepers", "You have the following keepers:", player.getKeepersFormatted()))
                .setEphemeral(true).queue();
    }
}
