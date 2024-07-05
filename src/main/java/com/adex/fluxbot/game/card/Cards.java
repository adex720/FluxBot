package com.adex.fluxbot.game.card;

import com.adex.fluxbot.game.rule.Rule;

public class Cards {

    public static final Card PLAY_TWO = new RuleCard(Rule.PLAY_COUNT, 2);
    public static final Card PLAY_THREE = new RuleCard(Rule.PLAY_COUNT, 3);
    public static final Card PLAY_FOUR = new RuleCard(Rule.PLAY_COUNT, 4);
    public static final Card PLAY_FIVE = new RuleCard(Rule.PLAY_COUNT, 5);
    public static final Card PLAY_ALL = new RuleCard(Rule.PLAY_COUNT, -1);

    public static final Card DRAW_TWO = new RuleCard(Rule.DRAW_COUNT, 2);
    public static final Card DRAW_THREE = new RuleCard(Rule.DRAW_COUNT, 3);
    public static final Card DRAW_FOUR = new RuleCard(Rule.DRAW_COUNT, 4);
    public static final Card DRAW_FIVE = new RuleCard(Rule.DRAW_COUNT, 5);

    public static final Card HAND_LIMIT_ZERO = new RuleCard(Rule.HAND_LIMIT, 0);
    public static final Card HAND_LIMIT_ONE = new RuleCard(Rule.HAND_LIMIT, 1);
    public static final Card HAND_LIMIT_TWO = new RuleCard(Rule.HAND_LIMIT, 2);
    public static final Card HAND_LIMIT_THREE = new RuleCard(Rule.HAND_LIMIT, 3);
    public static final Card HAND_LIMIT_FOUR = new RuleCard(Rule.HAND_LIMIT, 4);
    public static final Card HAND_LIMIT_FIVE = new RuleCard(Rule.HAND_LIMIT, 5);

    public static final Card KEEPER_LIMIT_TWO = new RuleCard(Rule.KEEPER_LIMIT, 2);
    public static final Card KEEPER_LIMIT_THREE = new RuleCard(Rule.KEEPER_LIMIT, 3);
    public static final Card KEEPER_LIMIT_FOUR = new RuleCard(Rule.KEEPER_LIMIT, 4);
    public static final Card KEEPER_LIMIT_FIVE = new RuleCard(Rule.KEEPER_LIMIT, 5);

    public static final Card ONE_HIDDEN_KEEPER = new RuleCard(Rule.KEEPERS_SECRET, 1);
    public static final Card ALL_KEEPERS_HIDDEN = new RuleCard(Rule.KEEPERS_SECRET, -1);

    public static final Card FINAL_CARD_RANDOM = new RuleCard(Rule.FINAL_CARD_RANDOM, 1);

}
