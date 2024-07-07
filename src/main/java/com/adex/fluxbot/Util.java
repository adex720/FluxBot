package com.adex.fluxbot;

public class Util {

    /**
     * Combines the count of something with its unit. Adds a plural s when count is not one.
     * For example parameters (5, horse) returns "5 horses" and (1 dog) returns "1 dog".
     *
     * @param count Amount
     * @param word  Singular form of what there are
     */
    public static String combineCountAndWord(int count, String word) {
        if (count == 1) return 1 + " " + word;
        return count + " " + word + "s";
    }

    /**
     * Combines the count of something with its unit. Uses plural word when count is not one.
     * For example parameters (5, cactus, cacti) returns "5 cacti" and (1 cactus, cacti) returns "1 cactus".
     *
     * @param count  Amount
     * @param word   Singular form of what there are
     * @param plural Plural form of what there are
     */
    public static String combineCountAndWord(int count, String word, String plural) {
        if (count == 1) return 1 + " " + word;
        return count + " " + plural;
    }
}
