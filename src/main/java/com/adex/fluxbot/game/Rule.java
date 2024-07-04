package com.adex.fluxbot.game;

/**
 * Defines rules states for the game.
 */
public enum Rule {

    PLAY_COUNT("Play", 0, -1, 5, 1, 0),
    DRAW_COUNT("Draw", 1, 1, 5, 1, 0),
    HAND_LIMIT("Hand Limit", 2, -1, 5, -1, 1),
    KEEPER_LIMIT("Keeper Limit", 3, 0, 5, 0, 1),
    KEEPERS_SECRET("Keepers hidden", 4, -1, 1, 0, 1),
    FINAL_CARD_RANDOM("Final card random", 5, 0, 0, 0, 2);

    Rule(String name, int id, int min, int max, int defaultValue, int displayStyle) {
        this.id = id;
        this.displayStyle = displayStyle;
        if (min > max)
            throw new IllegalArgumentException("Invalid rule settings: min value cannot be larger than max value");
        if (defaultValue < min)
            throw new IllegalArgumentException("Invalid rule settings: default value cannot be smaller than min value");
        if (defaultValue > max)
            throw new IllegalArgumentException("Invalid rule settings: default value cannot be larger than max value");

        this.name = name;
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
    }

    public static final int RULE_AMOUNT = Rule.values().length;

    public final String name;
    public final int id;

    // value of -1 means all or not enforced depending on the rule
    public final int min;
    public final int max;
    public final int defaultValue;

    // 0 = name + value
    // 1 = name + value if not default
    // 2 = name if not default
    // 3 = custom
    private final int displayStyle;

    /**
     * Formats the rule and its value to display format
     *
     * @param value current value of the rule
     * @return String displaying the rule. If the rule shouldn't be displayed, an empty string is returned.
     */
    public String display(int value) {
        String valueString = value == -1 ? "All" : "" + value;

        if (displayStyle == 0) {
            return name + ": " + valueString;
        }

        if (displayStyle == 1) {
            if (value == defaultValue) {
                return "";
            }
            return name + ": " + valueString;
        }

        if (displayStyle == 2) {
            if (value == defaultValue) {
                return "";
            }
            return name;
        }


        throw new IllegalStateException("Rule " + this.name + " has custom display but no display is defined for it");
    }

}
