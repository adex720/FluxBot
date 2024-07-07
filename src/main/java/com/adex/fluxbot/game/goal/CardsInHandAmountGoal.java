package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.Util;
import com.adex.fluxbot.game.Player;

/**
 * The goal is met when a player has an equal amount or more cards in hand.
 */
public class CardsInHandAmountGoal extends Goal {

    public CardsInHandAmountGoal(String name, int amount) {
        super(name);
        this.amount = amount;
    }

    public final int amount;

    @Override
    public boolean check(Player player) {
        return player.getHand().size() >= amount;
    }

    @Override
    public String getDescription() {
        return "has at least " + Util.combineCountAndWord(amount, "card") + " in their hand";
    }
}
