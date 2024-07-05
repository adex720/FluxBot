package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.keeper.Keeper;

import java.util.ArrayList;

/**
 * The goal is met when one player has both the keepers.
 */
public class Have2KeepersGoal extends Goal {

    public final Keeper keeper1;
    public final Keeper keeper2;

    public Have2KeepersGoal(String name, Keeper keeper1, Keeper keeper2) {
        super(name);

        this.keeper1 = keeper1;
        this.keeper2 = keeper2;
    }

    @Override
    public boolean check(Player player) {
        ArrayList<Keeper> keepers = player.getKeepers();
        return keepers.contains(keeper1) && keepers.contains(keeper2);
    }
}
