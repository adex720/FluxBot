package com.adex.fluxbot.discord.command;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.game.FluxGame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Random;

/**
 * Gives access to different information regarding a slash command interaction or a button press interaction.
 */
public class EventContext {

    private final FluxBot bot;

    private final boolean fromCommand;
    private final boolean fromButton;
    private final boolean fromAutoComplete;

    private final SlashCommandInteractionEvent slashCommandEvent;
    private final ButtonInteractionEvent buttonEvent;
    private final CommandAutoCompleteInteractionEvent autoCompleteEvent;

    private final User user;

    private Boolean inGame;
    private FluxGame game;

    public EventContext(SlashCommandInteractionEvent event, FluxBot bot) {
        this.bot = bot;
        fromCommand = true;
        fromButton = false;
        fromAutoComplete = false;

        slashCommandEvent = event;
        buttonEvent = null;
        autoCompleteEvent = null;

        user = event.getUser();

        inGame = null;
        game = null;
    }

    public EventContext(ButtonInteractionEvent event, FluxBot bot) {
        this.bot = bot;
        fromCommand = false;
        fromButton = true;
        fromAutoComplete = false;

        slashCommandEvent = null;
        buttonEvent = event;
        autoCompleteEvent = null;

        user = event.getUser();

        inGame = null;
        game = null;
    }

    public EventContext(CommandAutoCompleteInteractionEvent event, FluxBot bot) {
        this.bot = bot;
        fromCommand = false;
        fromButton = false;
        fromAutoComplete = true;

        slashCommandEvent = null;
        buttonEvent = null;
        autoCompleteEvent = event;

        user = event.getUser();

        inGame = null;
        game = null;
    }

    public boolean isFromCommand() {
        return fromCommand;
    }

    public Event getEvent() {
        if (fromCommand) return slashCommandEvent;
        if (fromButton) return buttonEvent;
        if (fromAutoComplete) return autoCompleteEvent;
        return null;
    }

    public SlashCommandInteractionEvent getSlashCommandEvent() {
        return slashCommandEvent;
    }

    public ButtonInteractionEvent getButtonEvent() {
        return buttonEvent;
    }

    public CommandAutoCompleteInteractionEvent getAutoCompleteEvent() {
        return autoCompleteEvent;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return user.getName();
    }

    public String getUserAsMention() {
        return "<@" + getUserId() + "";
    }

    public long getUserId() {
        return user.getIdLong();
    }

    public TextChannel getChannel() {
        if (fromCommand) return (TextChannel) slashCommandEvent.getChannel();
        if (fromButton) return (TextChannel) buttonEvent.getChannel();
        if (fromAutoComplete) return (TextChannel) autoCompleteEvent.getChannel();
        return null;
    }

    public FluxBot getBot() {
        return bot;
    }

    public JDA getJda() {
        return getEvent().getJDA();
    }

    public Random getRandom() {
        return bot.getRandom();
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
        FluxGame game = bot.getGameManager().getGameByUserId(getUserId());
        if (game != null) {
            inGame = true;
            this.game = game;
        } else {
            inGame = false;
        }
    }

    /**
     * Returns the name of the command.
     * If the command is a subcommand, only returns the first command name.
     * If this is from a {@link SlashCommandInteractionEvent}, returns the name of the used command.
     * If this is from a {@link CommandAutoCompleteInteractionEvent},
     * returns the name of the command having an autocompletable option.
     * Returns null if this is from different event.
     */
    public String getCommandName() {
        if (fromCommand) return slashCommandEvent.getCommandString();
        if (fromAutoComplete) return autoCompleteEvent.getName();
        return null;
    }

    /**
     * Returns the full name of the command.
     * If the command is a subcommand, returns the whole command name.
     * If this is from a {@link SlashCommandInteractionEvent}, returns the name of the used command.
     * If this is from a {@link CommandAutoCompleteInteractionEvent},
     * returns the name of the command having an autocompletable option.
     * Returns null if this is from different event.
     */
    public String getFullCommandName() {
        if (fromCommand) return slashCommandEvent.getFullCommandName();
        if (fromAutoComplete) return autoCompleteEvent.getFullCommandName();
        return null;
    }

    /**
     * Returns the name of the option being autocompleted.
     * Returns null if this is from different event.
     */
    public String getOptionName() {
        if (!fromAutoComplete) return null;
        return autoCompleteEvent.getFocusedOption().getName();
    }

    /**
     * Returns the {@link OptionMapping} of the option with the given name when the command was used.
     * Returns null if this is from different event.
     */
    public OptionMapping getOption(String option) {
        if (!fromCommand) return null;
        return slashCommandEvent.getOption(option);
    }

    /**
     * Returns the button pressed.
     * Returns null if this is from different event.
     */
    public Button getButton() {
        if (!fromButton) return null;
        return buttonEvent.getButton();
    }

    /**
     * Returns what is typed in the autocompletable option.
     * Returns null if this is from different event.
     */
    public String getTyped() {
        if (!fromAutoComplete) return null;
        return autoCompleteEvent.getFocusedOption().getValue();
    }
}
