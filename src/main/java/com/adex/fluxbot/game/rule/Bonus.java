package com.adex.fluxbot.game.rule;

import com.adex.fluxbot.game.keeper.Keeper;
import com.adex.fluxbot.game.keeper.Keepers;

public enum Bonus {

    NONE("No bonus", 0, null, null),
    TIME("Time Bonus", 1, Rule.PLAY_COUNT, Keepers.TIME),
    TAX("Tax Bonus", 2, Rule.DRAW_COUNT, Keepers.TAXES),
    BRAIN("Brain Bonus", 3, Rule.HAND_LIMIT, Keepers.THE_BRAIN),
    MONEY("Money Bonus", 4, Rule.KEEPER_LIMIT, Keepers.MONEY);

    public final String name;
    public final int id;
    public final Rule rule;
    public final Keeper keeper;

    Bonus(String name, int id, Rule rule, Keeper keeper) {
        this.name = name;
        this.id = id;
        this.rule = rule;
        this.keeper = keeper;
    }

    public static Bonus getById(int id) {
        return switch (id) {
            case 0 -> NONE;
            case 1 -> TIME;
            case 2 -> TAX;
            case 3 -> BRAIN;
            case 4 -> MONEY;
            default -> throw new IllegalStateException("Unexpected bonus id, should be in range [0, 4]: " + id);
        };
    }
}
