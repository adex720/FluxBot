package com.adex.fluxbot.discord.listeners;


import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.card.Card;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteRule {

    public final String commandName;
    public final String optionName;

    private final OptionSelector selector;

    public AutoCompleteRule(String commandName, String optionName, OptionSelector selector) {
        this.commandName = commandName;
        this.optionName = optionName;
        this.selector = selector;
    }

    public boolean match(String command, String option) {
        return commandName.equals(command) && optionName.equals(option);
    }

    public List<Command.Choice> select(EventContext context) {
        List<Command.Choice> result = selector.select(context);
        context.getAutoCompleteEvent().replyChoices(result).queue();
        return result;
    }

    /**
     * Chooses which options to display from the list of all possible values.
     */
    public interface OptionSelector {
        List<Command.Choice> select(EventContext context);
    }

    public static final OptionSelector CHOOSE_CARD_FROM_HAND = (context) -> {
        List<Command.Choice> list = new ArrayList<>();
        FluxGame game = context.getGame();
        if (game == null) return list; // User is not in a game so returning empty list.

        long userId = context.getUserId();
        Player player = game.getPlayerByUserId(userId);

        String input = context.getTyped();
        for (Card card : player.getHand()) {
            if (card.name.contains(input)) list.add(card.getAsChoice());
        }

        return list;
    };

}
