package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.Util;
import com.adex.fluxbot.game.Player;

/**
 * The goal is met when a player has more or the same amount of keepers as the value.
 */
public class KeeperAmountGoal extends Goal {

    public KeeperAmountGoal(String name, int amount) {
        super(name);
        this.amount = amount;
    }

    public final int amount;

    @Override
    public boolean check(Player player) {
        return player.getKeepers().size() >= amount;
    }

    @Override
    public String getDescription() {
        return "at least " + Util.combineCountAndWord(amount, "keeper") + " in front of them";
    }
}
