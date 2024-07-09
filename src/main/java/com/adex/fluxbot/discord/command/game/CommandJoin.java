package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.GameManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandJoin extends Command {

    public CommandJoin() {
        super("join", "Join a game");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Join game", "Cannot join game",
                    "You are already in a game")).setEphemeral(true).queue();
            return;
        }

        long userId = context.getUserId();
        GameManager gameManager = context.getBot().getGameManager();
        long joinId = context.getOption("game").getAsUser().getIdLong();
        FluxGame game = gameManager.getGameByUserId(joinId);

        if (game == null) {
            event.replyEmbeds(MessageCreator.createDefault("Join game", "Cannot find game",
                    "<@" + joinId + "> is not in a game")).setEphemeral(true).queue();
            return;
        }

        if (game.isFull()) {
            event.replyEmbeds(MessageCreator.createDefault("Join game", "Cannot join game",
                    "The game is already full")).setEphemeral(true).queue();
            return;
        }

        if (game.isInviteNeeded() && !game.isInvited(userId)) {
            event.replyEmbeds(MessageCreator.createDefault("Join game", "Cannot join game",
                    "You need an invite to join this game")).setEphemeral(true).queue();
            return;
        }

        game.addPlayer(userId, context.getUsername());
        event.replyEmbeds(MessageCreator.createDefault("Join game", "You joined the game", MessageCreator.COMMAND_TIPS)).queue();
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "game", "Player in the game to join", true)};
    }
}
