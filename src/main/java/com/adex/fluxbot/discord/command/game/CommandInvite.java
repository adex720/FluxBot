package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.GameManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandInvite extends Command {

    public CommandInvite() {
        super("invite", "Invite a player to a game");
    }

    @Override
    public void execute(EventContext context) {
        SlashCommandInteractionEvent event = context.getSlashCommandEvent();
        if (!context.inGame()) {
            event.replyEmbeds(MessageCreator.createDefault("Invite", "Cannot invite user",
                    "You are not in a game")).setEphemeral(true).queue();
            return;
        }

        long userId = context.getUserId();
        FluxGame game = context.getGame();

        if (!game.canInvite(userId)) {
            event.replyEmbeds(MessageCreator.createDefault("Cannot invite user",
                            "Only the game host can invite new players in this game",
                            "Ask " + game.getHostAsMention() + " to /invite the user or change the setting"))
                    .setEphemeral(true).queue();
            return;
        }

        GameManager gameManager = context.getBot().getGameManager();
        long inviteId = context.getOption("game").getAsUser().getIdLong();

        if (game.isInvited(inviteId)) {
            event.replyEmbeds(MessageCreator.createDefault("Invite", "Cannot invite user",
                    "<@" + inviteId + "> is already invited to the game")).setEphemeral(true).queue();
            return;
        }

        if (gameManager.getGameByUserId(inviteId) != null) {
            event.replyEmbeds(MessageCreator.createDefault("Invite", "Cannot invite user",
                    "<@" + inviteId + "> is already in a game")).setEphemeral(true).queue();
            return;
        }

        game.invite(inviteId);
        event.reply(context.getUserAsMention() + " has invited <@" + inviteId + "> to the game. Use /join " + context.getUsername() + " to join").queue();
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "user", "User to invite", true)};
    }
}

