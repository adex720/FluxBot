package com.adex.fluxbot.game.goal;

import com.adex.fluxbot.game.keeper.Keepers;

public class Goals {

    public static final Goal NO_GOAL = new NoGoal();

    public static final Goal MILK_AND_COOKIES = new Have2KeepersGoal("Milk and cookies", Keepers.MILK, Keepers.COOKIES);
    public static final Goal CHOCOLATE_AND_MILK = new Have2KeepersGoal("Chocolate Milk", Keepers.CHOCOLATE, Keepers.MILK);
    public static final Goal CHOCOLATE_AND_COOKIES = new Have2KeepersGoal("Chocolate Cookies", Keepers.CHOCOLATE, Keepers.COOKIES);
    public static final Goal DEATH_AND_CHOCOLATE = new Have2KeepersGoal("Death by Chocolate", Keepers.DEATH, Keepers.CHOCOLATE);
    public static final Goal WAR_AND_DEATH = new Have2KeepersGoal("War and Death", Keepers.WAR, Keepers.DEATH);
    public static final Goal DEATH_AND_TAXES = new Have2KeepersGoal("Death and Taxes", Keepers.DEATH, Keepers.TAXES);
    public static final Goal COFFEE_AND_DOUGHNUTS = new Have2KeepersGoal("Coffee and Doughnuts", Keepers.COFFEE, Keepers.DOUGHNUTS);
    public static final Goal SUN_AND_MOON = new Have2KeepersGoal("The Sun & The Moon", Keepers.THE_SUN, Keepers.THE_MOON);
    public static final Goal ROCKET_AND_MOON = new Have2KeepersGoal("Rocket to the Moon", Keepers.THE_ROCKET, Keepers.THE_MOON);
    public static final Goal BRAIN_AND_EYE = new Have2KeepersGoal("The mind's Eye", Keepers.THE_BRAIN, Keepers.THE_EYE);
    public static final Goal EYE_AND_PYRAMID = new Have2KeepersGoal("The Great Seal", Keepers.THE_EYE, Keepers.THE_PYRAMID);
    public static final Goal TOASTER_AND_TV = new Have2KeepersGoal("The Appliances", Keepers.THE_TOASTER, Keepers.TELEVISION);
    public static final Goal BREAD_AND_TOASTER = new Have2KeepersGoal("Toast", Keepers.BREAD, Keepers.THE_TOASTER);
    public static final Goal LOVE_AND_PEACE = new Have2KeepersGoal("Hippyism", Keepers.PEACE, Keepers.LOVE);
    public static final Goal TIME_AND_MONEY = new Have2KeepersGoal("Time is money", Keepers.TIME, Keepers.MONEY);

    public static final Goal MONEY_WITHOUT_TAXES = new HaveKeeperWithoutOtherGoal("Money no Taxes", Keepers.MONEY, Keepers.TAXES);
    public static final Goal THE_BRAIN_WITHOUT_TV = new HaveKeeperWithoutOtherGoal("Brains no TV", Keepers.THE_BRAIN, Keepers.TELEVISION);
    public static final Goal PEACE_WITHOUT_WAR = new HaveKeeperWithoutOtherGoal("Peace no War", Keepers.PEACE, Keepers.WAR);

    public static final Goal ONLY_LOVE = new HaveOneSpecificKeeperGoal("", Keepers.LOVE);

    public static final Goal FIVE_KEEPERS = new KeeperAmountGoal("5 Keepers", 5);
    public static final Goal TEN_CARDS_IN_HAND = new CardsInHandAmountGoal("10 Cards in Hand", 10);

}
