package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandHand extends Command {

    public CommandHand() {
        super("hand", "View your hand");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Hand", "Cannot view hand", "You are not in a game"))
                    .setEphemeral(true).queue();
            return;
        }

        FluxGame game = context.getGame();
        if (!game.hasStarted()) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot view hand", "The game has not started", "Star the game with /start"))
                    .setEphemeral(true).queue();
            return;
        }

        Player player = game.getPlayerByUserId(context.getUserId());
        event.replyEmbeds(MessageCreator.createDefault("Hand", "You have the following cards:", player.getHandFormatted()))
                .setEphemeral(true).queue();
    }
}
