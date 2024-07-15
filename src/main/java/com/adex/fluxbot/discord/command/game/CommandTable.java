package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandTable extends Command {

    public CommandTable() {
        super("table", "View general information about the game");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Table", "Cannot view the table", "You are not in a game"))
                    .setEphemeral(true).queue();
            return;
        }

        FluxGame game = context.getGame();
        if (!game.hasStarted()) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot view the table", "The game has not started", "Star the game with /start"))
                    .setEphemeral(true).queue();
            return;
        }

        // Adding all fields to one array
        int playerCount = game.getPlayerCount();
        MessageEmbed.Field[] fields = new MessageEmbed.Field[playerCount + 2];

        // Adding rules and goal
        fields[0] = new MessageEmbed.Field("Rules", game.getRules(), true);
        fields[1] = new MessageEmbed.Field("Goal", game.getGoal().name, true);

        // Adding player hands and keepers starting from index 2
        int playerId = game.currentPlayerId(); // Starting from player whose turn it is
        for (int i = 2; i < playerCount; i++) {
            fields[i] = game.getPlayerByPlayerId(playerId).getInfoField();
            playerId++;
            if (playerId >= playerCount) playerId = 0;
        }

        event.replyEmbeds(MessageCreator.createDefault("Flux game", fields)).queue();
    }
}