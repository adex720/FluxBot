package com.adex.fluxbot.discord.listeners;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.command.Commands;
import com.adex.fluxbot.discord.command.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AutoCompleteListener extends ListenerAdapter {

    private final FluxBot bot;
    private final Set<AutoCompleteRule> rules;

    private final HashMap<Long, Long> cooldowns;
    public static final int COOLDOWN = 1000; // only giving options every 1000 ms

    public AutoCompleteListener(FluxBot bot) {
        this.bot = bot;
        rules = new HashSet<>();
        cooldowns = new HashMap<>();
    }

    public void initRules() {
        addRule(new AutoCompleteRule(Commands.COMMAND_PLAY.getName(), "card", AutoCompleteRule.CHOOSE_CARD_FROM_HAND));
        addRule(new AutoCompleteRule(Commands.COMMAND_DISCARD.getName(), "card", AutoCompleteRule.CHOOSE_CARD_FROM_HAND));
    }

    public void addRule(AutoCompleteRule rule) {
        rules.add(rule);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        long userId = event.getUser().getIdLong();
        long time = event.getTimeCreated().toInstant().toEpochMilli();
        if (isOnCooldown(userId, time)) return;
        addCooldown(userId, time);

        EventContext context = new EventContext(event, bot);
        String commandName = context.getFullCommandName();
        String optionName = context.getOptionName();
        for (AutoCompleteRule rule : rules) {
            if (rule.match(commandName, optionName)) {
                rule.select(context);
                return;
            }
        }

        // No rule was found
        event.replyChoices().queue();
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
