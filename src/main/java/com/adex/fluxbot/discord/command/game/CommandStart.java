package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandStart extends Command {

    public CommandStart() {
        super("start", "Start your game");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot start game", "You are not in a game",
                    "Create a new game with /create")).setEphemeral(true).queue();
            return;
        }

        long userId = context.getUserId();
        FluxGame game = context.getGame();
        if (game.getTurnState() != FluxGame.TurnState.NOT_STARTED) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot start game", "Your game is already playing",
                    MessageCreator.COMMAND_TIPS)).setEphemeral(true).queue();
            return;
        }

        if (game.getHostUserId() != userId) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot start game", "Only the game host can start the game",
                    "Ask " + game.getHostAsMention() + " to start the game")).setEphemeral(true).queue();
            return;
        }

        if (game.getPlayerCount() < FluxGame.MIN_PLAYER_COUNT) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot start game", "The game doesn't have enough players to start",
                    game.getPlayerCount() + "/" + FluxGame.MAX_PLAYER_COUNT + " players required")).setEphemeral(true).queue();
            return;
        }

        game.startGame(context);
        event.replyEmbeds(MessageCreator.createDefault("Starting game")).queue();
    }
}
