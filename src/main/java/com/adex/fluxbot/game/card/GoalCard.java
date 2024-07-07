package com.adex.fluxbot.game.card;

import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.goal.Goal;

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
    }
}
