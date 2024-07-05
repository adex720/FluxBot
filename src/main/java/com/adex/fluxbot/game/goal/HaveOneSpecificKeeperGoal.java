package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.keeper.Keeper;

/**
 * The goal is met when the player has the keeper and no other keepers.
 */
public class HaveOneSpecificKeeperGoal extends Goal {

    public final Keeper keeper;

    public HaveOneSpecificKeeperGoal(String name, Keeper keeper) {
        super(name);

        this.keeper = keeper;
    }

    @Override
    public boolean check(Player player) {
        return player.getKeepers().size() == 1 && player.getKeepers().get(0) == keeper;
    }
}
