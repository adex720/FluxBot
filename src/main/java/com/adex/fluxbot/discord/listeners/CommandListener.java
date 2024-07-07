package com.adex.fluxbot.discord.listeners;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.game.CommandPlay;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashSet;
import java.util.Set;

public class CommandListener extends ListenerAdapter {

    private final FluxBot bot;

    private final Set<Command> commands;

    public CommandListener(FluxBot bot) {
        this.bot = bot;

        commands = new HashSet<>();
    }

    /**
     * Initializes commands and adds them to the commands set.
     */
    public void initCommands() {
        addCommand(new CommandPlay());
    }

    /**
     * Adds a command to the commands set and gives it an id.
     */
    private void addCommand(Command command) {
        command.setId(commands.size());
        commands.add(command);
    }

    public int getCommandAmount() {
        return commands.size();
    }

    /**
     * Registers commands to Discord.
     */
    public void registerCommands(JDA jda) {
        Set<CommandData> data = new HashSet<>();
        for (Command command : commands) {
            data.add(command.getCommandData());
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) return;

        String commandName = event.getName();
        long userId = event.getUser().getIdLong();
    }
}
