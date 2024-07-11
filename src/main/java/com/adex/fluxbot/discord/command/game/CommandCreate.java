package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandCreate extends Command {

    public CommandCreate() {
        super("create", "Create a new game");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("New game", "Cannot create game",
                    "You are already in a game")).setEphemeral(true).queue();
            return;
        }

        long userId = context.getUserId();
        FluxGame game = context.getBot().getGameManager().createGame(context.getBot(), userId, context.getUsername(), context.getChannel());
        event.replyEmbeds(MessageCreator.createDefault("New game", context.getUsername() + " created a new game",
                "Invite users with /invite\nJoin with /join " + context.getUserAsMention())).queue();
    }
}
