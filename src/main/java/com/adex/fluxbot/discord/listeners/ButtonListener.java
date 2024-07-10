package com.adex.fluxbot.discord.listeners;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.button.ButtonManager;
import com.adex.fluxbot.discord.command.EventContext;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ButtonListener extends ListenerAdapter {

    private final FluxBot bot;

    private final Set<ButtonManager> buttonManagers;

    public ButtonListener(FluxBot bot) {
        this.bot = bot;
        this.buttonManagers = new HashSet<>();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        // Checking cooldown
        long userId = event.getUser().getIdLong();
        long time = event.getTimeCreated().toInstant().toEpochMilli();
        if (isOnCooldown(userId, time)) return;
        addCooldown(userId, time);

        EventContext context = new EventContext(event, bot);
        String buttonId = context.getButtonIdSplit()[0];
        for (ButtonManager manager : buttonManagers) {
            if (manager.matches(buttonId)) {
                try {
                    manager.onButtonPress(context);
                } catch (Exception e) {
                    context.getChannel().sendMessageEmbeds(MessageCreator.createErrorMessage(e)).queue();
                    context.getBot().getLogger().error("Error when executing button {}: {}\n{}",
                            buttonId, e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
                return;
            }
        }

        bot.getLogger().warn("Button id {} doesn't have any managers", context.getButtonId());
    }

    public void addButtonManager(ButtonManager manager) {
        buttonManagers.add(manager);
    }

    /**
     * Buttons use same cooldown as commands
     *
     * @param userId Discord user id of the user
     * @param time   Epoch milliseconds when the interaction was created
     */
    public boolean isOnCooldown(long userId, long time) {
        return bot.getCommandListener().isOnCooldown(userId, time);
    }

    /**
     * Buttons use same cooldown as commands
     *
     * @param userId Discord user id of the user
     * @param time   Epoch milliseconds when the interaction was created
     */
    public void addCooldown(long userId, long time) {
        bot.getCommandListener().addCooldown(userId, time);
    }
}
