package com.adex.fluxbot.discord.listeners;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.game.CommandDiscard;
import com.adex.fluxbot.discord.command.game.CommandPlay;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class CommandListener extends ListenerAdapter {

    private final HashMap<Long, Long> cooldowns;
    public static final int COOLDOWN = 500;
    // minimum time difference between 2 interactions in milliseconds.
    // If when making an interaction the cooldown is on, no response will be given or action done.

    private final FluxBot bot;

    private final Set<Command> commands;

    public CommandListener(FluxBot bot) {
        this.bot = bot;

        commands = new HashSet<>();

        cooldowns = new HashMap<>();
    }

    /**
     * Initializes commands and adds them to the commands setRule.
     */
    public void initCommands() {
        addCommand(new CommandPlay());
        addCommand(new CommandDiscard());
    }

    /**
     * Adds a command to the commands setRule and gives it an id.
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

        jda.updateCommands().addCommands(data).queue();

        bot.getLogger().info("Registered all commands");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) return;

        String commandName = event.getName();
        long userId = event.getUser().getIdLong();

        long time = event.getTimeCreated().toInstant().toEpochMilli();

        if (isOnCooldown(userId, time)) return;
        addCooldown(userId, time);
    }

    public void addCooldown(long userId, long time) {
        cooldowns.put(userId, time + COOLDOWN);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cooldowns.remove(userId, time + COOLDOWN);
            }
        }, COOLDOWN);
    }

    public boolean isOnCooldown(long userId, long time) {
        Long cooldownEnds = cooldowns.get(userId);
        if (cooldownEnds == null) return false;
        return cooldownEnds > time;
    }
}
