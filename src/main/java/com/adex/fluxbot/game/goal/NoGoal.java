package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.game.Player;

/**
 * Used as goal when no goal cards are yet played .
 */
public class NoGoal extends Goal {

    public NoGoal() {
        super("None");
    }

    @Override
    public boolean check(Player player) {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
