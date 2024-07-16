package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.game.Player;

/**
 * If a player achieves a goal at any point, they will win the game.
 */
public abstract class Goal {

    private static int GOAL_COUNT = 0;

    public final String name;
    public final int id;

    protected Goal(String name) {
        this.name = name;
        id = GOAL_COUNT++;
    }

    public abstract boolean check(Player player);

    /**
     * Should be in a format which makes sense when used in "to have DESCRIPTION".
     * Is used in for example: "Player wins if they have DESCRIPTION".
     */
    public abstract String getDescription();
}
