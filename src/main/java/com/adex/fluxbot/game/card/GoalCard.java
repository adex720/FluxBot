package com.adex.fluxbot.game.card;

import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.goal.Goal;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Changes the goal of the game.
 */
public class GoalCard extends Card {

    public GoalCard(Goal goal) {
        super(goal.name, Type.GOAL);
        this.goal = goal;
    }

    public final Goal goal;

    @Override
    public void onPlay(FluxGame game, EventContext context) {
        game.setGoal(goal);
        context.getSlashCommandEvent().replyEmbeds(getPlayMessage(context.getUsername())).queue();
    }

    public MessageEmbed getPlayMessage(String username) {
        return getPlayMessage(username, "The new goal is to have " + goal.getDescription());
    }
}
