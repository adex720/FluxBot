package com.adex.fluxbot.game.card;

import com.adex.fluxbot.game.goal.Goals;
import com.adex.fluxbot.game.keeper.Keepers;
import com.adex.fluxbot.game.rule.Rule;

public class Cards {

    public static final Card RULE_PLAY_TWO = new RuleCard(Rule.PLAY_COUNT, 2);
    public static final Card RULE_PLAY_THREE = new RuleCard(Rule.PLAY_COUNT, 3);
    public static final Card RULE_PLAY_FOUR = new RuleCard(Rule.PLAY_COUNT, 4);
    public static final Card RULE_PLAY_FIVE = new RuleCard(Rule.PLAY_COUNT, 5);
    public static final Card RULE_PLAY_ALL = new RuleCard(Rule.PLAY_COUNT, -1);

    public static final Card RULE_DRAW_TWO = new RuleCard(Rule.DRAW_COUNT, 2);
    public static final Card RULE_DRAW_THREE = new RuleCard(Rule.DRAW_COUNT, 3);
    public static final Card RULE_DRAW_FOUR = new RuleCard(Rule.DRAW_COUNT, 4);
    public static final Card RULE_DRAW_FIVE = new RuleCard(Rule.DRAW_COUNT, 5);

    public static final Card RULE_HAND_LIMIT_ZERO = new RuleCard(Rule.HAND_LIMIT, 0);
    public static final Card RULE_HAND_LIMIT_ONE = new RuleCard(Rule.HAND_LIMIT, 1);
    public static final Card RULE_HAND_LIMIT_TWO = new RuleCard(Rule.HAND_LIMIT, 2);
    public static final Card RULE_HAND_LIMIT_THREE = new RuleCard(Rule.HAND_LIMIT, 3);
    public static final Card RULE_HAND_LIMIT_FOUR = new RuleCard(Rule.HAND_LIMIT, 4);
    public static final Card RULE_HAND_LIMIT_FIVE = new RuleCard(Rule.HAND_LIMIT, 5);

    public static final Card RULE_KEEPER_LIMIT_TWO = new RuleCard(Rule.KEEPER_LIMIT, 2);
    public static final Card RULE_KEEPER_LIMIT_THREE = new RuleCard(Rule.KEEPER_LIMIT, 3);
    public static final Card RULE_KEEPER_LIMIT_FOUR = new RuleCard(Rule.KEEPER_LIMIT, 4);
    public static final Card RULE_KEEPER_LIMIT_FIVE = new RuleCard(Rule.KEEPER_LIMIT, 5);

    public static final Card RULE_ONE_HIDDEN_KEEPER = new RuleCard("Secret Data", Rule.KEEPERS_SECRET, 1);
    public static final Card RULE_ALL_KEEPERS_HIDDEN = new RuleCard("Government Cover-Up", Rule.KEEPERS_SECRET, -1);

    public static final Card RULE_FINAL_CARD_RANDOM = new RuleCard(Rule.FINAL_CARD_RANDOM, 1);


    public static final Card THE_BRAIN = new KeeperCard(Keepers.THE_BRAIN);
    public static final Card THE_TOASTER = new KeeperCard(Keepers.THE_TOASTER);
    public static final Card TELEVISION = new KeeperCard(Keepers.TELEVISION);
    public static final Card MONEY = new KeeperCard(Keepers.MONEY);
    public static final Card PEACE = new KeeperCard(Keepers.PEACE);
    public static final Card BREAD = new KeeperCard(Keepers.BREAD);
    public static final Card CHOCOLATE = new KeeperCard(Keepers.CHOCOLATE);
    public static final Card TIME = new KeeperCard(Keepers.TIME);
    public static final Card DEATH = new KeeperCard(Keepers.DEATH);
    public static final Card THE_MOON = new KeeperCard(Keepers.THE_MOON);
    public static final Card WAR = new KeeperCard(Keepers.WAR);
    public static final Card DOUGHNUTS = new KeeperCard(Keepers.DOUGHNUTS);
    public static final Card COFFEE = new KeeperCard(Keepers.COFFEE);
    public static final Card LOVE = new KeeperCard(Keepers.LOVE);
    public static final Card MILK = new KeeperCard(Keepers.MILK);
    public static final Card TAXES = new KeeperCard(Keepers.TAXES);
    public static final Card THE_SUN = new KeeperCard(Keepers.THE_SUN);
    public static final Card THE_PYRAMID = new KeeperCard(Keepers.THE_PYRAMID);
    public static final Card THE_ROCKET = new KeeperCard(Keepers.THE_ROCKET);
    public static final Card THE_EYE = new KeeperCard(Keepers.THE_EYE);
    public static final Card COOKIES = new KeeperCard(Keepers.COOKIES);


    public static final Card GOAL_MILK_AND_COOKIES = new GoalCard(Goals.MILK_AND_COOKIES);
    public static final Card GOAL_CHOCOLATE_AND_MILK = new GoalCard(Goals.CHOCOLATE_AND_MILK);
    public static final Card GOAL_CHOCOLATE_AND_COOKIES = new GoalCard(Goals.CHOCOLATE_AND_COOKIES);
    public static final Card GOAL_DEATH_AND_CHOCOLATE = new GoalCard(Goals.DEATH_AND_CHOCOLATE);
    public static final Card GOAL_WAR_AND_DEATH = new GoalCard(Goals.WAR_AND_DEATH);
    public static final Card GOAL_DEATH_AND_TAXES = new GoalCard(Goals.DEATH_AND_TAXES);
    public static final Card GOAL_COFFEE_AND_DOUGHNUTS = new GoalCard(Goals.COFFEE_AND_DOUGHNUTS);
    public static final Card GOAL_SUN_AND_MOON = new GoalCard(Goals.SUN_AND_MOON);
    public static final Card GOAL_ROCKET_AND_MOON = new GoalCard(Goals.ROCKET_AND_MOON);
    public static final Card GOAL_BRAIN_AND_EYE = new GoalCard(Goals.BRAIN_AND_EYE);
    public static final Card GOAL_EYE_AND_PYRAMID = new GoalCard(Goals.EYE_AND_PYRAMID);
    public static final Card GOAL_TOASTER_AND_TV = new GoalCard(Goals.TOASTER_AND_TV);
    public static final Card GOAL_BREAD_AND_TOASTER = new GoalCard(Goals.BREAD_AND_TOASTER);
    public static final Card GOAL_LOVE_AND_PEACE = new GoalCard(Goals.LOVE_AND_PEACE);
    public static final Card GOAL_TIME_AND_MONEY = new GoalCard(Goals.TIME_AND_MONEY);

    public static final Card GOAL_MONEY_WITHOUT_TAXES = new GoalCard(Goals.MONEY_WITHOUT_TAXES);
    public static final Card GOAL_THE_BRAIN_WITHOUT_TV = new GoalCard(Goals.THE_BRAIN_WITHOUT_TV);
    public static final Card GOAL_PEACE_WITHOUT_WAR = new GoalCard(Goals.PEACE_WITHOUT_WAR);

    public static final Card GOAL_ONLY_LOVE = new GoalCard(Goals.ONLY_LOVE);

    public static final Card GOAL_FIVE_KEEPERS = new GoalCard(Goals.FIVE_KEEPERS);
    public static final Card GOAL_TEN_CARDS_IN_HAND = new GoalCard(Goals.TEN_CARDS_IN_HAND);

    public static int getCardAmount() {
        return Card.getCardAmount();
    }

}