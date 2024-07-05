package com.adex.fluxbot.game.card;

import com.adex.fluxbot.game.Flux;
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
    public void onPlay(Flux game) {
        game.setGoal(goal);
    }
}
