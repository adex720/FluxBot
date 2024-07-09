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

    // The largest positive integer whose square is less or equal the maximum Integer value.
    public static final int MAX_INTEGER_SQRT = (int) Math.floor(Integer.MAX_VALUE);

    /**
     * Calculates the integer value of a to the power of b.
     * Throws {@link ArithmeticException} if the value is higher than {@link Integer#MAX_VALUE}.
     * Throws {@link IllegalArgumentException} if b is negative.
     * Efficiency: log(b)
     *
     * @param a base
     * @param b exponent, has to be non-negative
     */
    public static int intPow(int a, int b) {
        if (b <= 1) {
            if (b == 1) return a;
            if (b == 0) return 1;
            throw new IllegalArgumentException();
        }
        int c = intPow(a, b >> 1);
        return (b & 1) == 0 ? c * c : c * c * a;
    }
}
