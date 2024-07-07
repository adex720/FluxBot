package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.keeper.Keeper;

/**
 * Goal is met when a player has the winning keeper and the disallowed keeper is not in front of any player.
 */
public class HaveKeeperWithoutOtherGoal extends Goal {

    public HaveKeeperWithoutOtherGoal(String name, Keeper winning, Keeper notAllowed) {
        super(name);
        this.winning = winning;
        this.notAllowed = notAllowed;
    }

    public final Keeper winning;
    public final Keeper notAllowed;

    @Override
    public boolean check(Player player) {
        if (!player.getKeepers().contains(winning)) return false;

        for (Player checking : player.getGame().getPlayers()) {
            if (checking.getKeepers().contains(notAllowed)) return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return winning.getEmoteAndName() + " in front of them and no player has " + notAllowed.getEmoteAndName() + " in front of them";
    }
}
