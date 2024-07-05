package com.adex.fluxbot.game.keeper;

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
}
