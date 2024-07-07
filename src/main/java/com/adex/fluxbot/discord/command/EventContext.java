package com.adex.fluxbot.discord.command;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.game.FluxGame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Gives access to different information regarding a slash command interaction or a button press interaction.
 */
public class EventContext {

    private final FluxBot bot;

    private final boolean fromCommand;
    private final SlashCommandInteractionEvent slashCommandEvent;
    private final ButtonInteractionEvent buttonEvent;

    private final User user;
    private final MessageChannel channel;

    private Boolean inGame;
    private FluxGame game;

    public EventContext(SlashCommandInteractionEvent event, FluxBot bot) {
        this.bot = bot;
        fromCommand = false;
        slashCommandEvent = event;
        buttonEvent = null;

        user = event.getUser();
        channel = event.getChannel();

        inGame = null;
        game = null;
    }

    public boolean isFromCommand() {
        return fromCommand;
    }

    public SlashCommandInteractionEvent getSlashCommandEvent() {
        return slashCommandEvent;
    }

    public ButtonInteractionEvent getButtonEvent() {
        return buttonEvent;
    }

    public User getUser() {
        return user;
    }

    public long getUserId() {
        return user.getIdLong();
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public FluxBot getBot() {
        return bot;
    }

    public JDA getJda() {
        return (fromCommand ? slashCommandEvent : buttonEvent).getJDA();
    }

    public boolean inGame() {
        if (inGame == null) {
            findGame();
        }

        return inGame;
    }

    public FluxGame getGame() {
        if (inGame == null) {
            findGame();
        }

        return game;
    }

    /**
     * Sets the values for {@link this.game} and {@link this.inGame}.
     */
    private void findGame() {
        FluxGame game = bot.gameManager.getGameByUserId(getUserId());
        if (game != null) {
            inGame = true;
            this.game = game;
        } else {
            inGame = false;
        }
    }

    public OptionMapping getOption(String option) {
        if (!fromCommand) return null;
        return slashCommandEvent.getOption(option);
    }
}
