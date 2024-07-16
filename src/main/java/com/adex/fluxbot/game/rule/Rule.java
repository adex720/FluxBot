package com.adex.fluxbot.game.rule;

/**
 * Defines rules states for the game.
 */
public enum Rule {

    PLAY_COUNT("Play", 0, -1, 5, 1, DisplayStyle.ALWAYS),
    DRAW_COUNT("Draw", 1, 1, 5, 1, DisplayStyle.ALWAYS),
    HAND_LIMIT("Hand Limit", 2, -1, 5, -1, DisplayStyle.WHEN_NOT_DEFAULT),
    KEEPER_LIMIT("Keeper Limit", 3, -1, 5, -1, DisplayStyle.WHEN_NOT_DEFAULT),
    KEEPERS_SECRET("Keepers hidden", 4, -1, 1, 0, DisplayStyle.WHEN_NOT_DEFAULT),
    FINAL_CARD_RANDOM("Final card random", 5, 0, 0, 0, DisplayStyle.ONLY_NAME_AND_WHEN_NOT_DEFAULT),
    BONUS("Bonus", 6, 0, 4, 0, DisplayStyle.CUSTOM);

    Rule(String name, int id, int min, int max, int defaultValue, DisplayStyle displayStyle) {
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

    private final DisplayStyle displayStyle;

    /**
     * Formats the rule and its value to display format
     *
     * @param value current value of the rule
     * @return String displaying the rule. If the rule shouldn't be displayed, an empty string is returned.
     */
    public String display(int value) {
        String valueString = value == -1 ? "All" : "" + value;

        if (displayStyle == DisplayStyle.ALWAYS) {
            return name + ": " + valueString;
        }

        if (displayStyle == DisplayStyle.WHEN_NOT_DEFAULT) {
            if (value == defaultValue) {
                return "";
            }
            return name + ": " + valueString;
        }

        if (displayStyle == DisplayStyle.ONLY_NAME_AND_WHEN_NOT_DEFAULT) {
            if (value == defaultValue) {
                return "";
            }
            return name;
        }

        if (this == BONUS) {
            return switch (value) {
                case 0 -> "No bonus";
                case 1 -> "Time bonus";
                case 2 -> "Tax bonus";
                case 3 -> "Brain bonus";
                case 4 -> "Money bonus";
                default -> throw new IllegalStateException("Illegal value for rule Bonus, should be in range [0, 4], received: " + value);
            };
        }

        throw new IllegalStateException("Rule " + this.name + " has custom display but no display is defined for it");
    }

    /**
     * Returns a string containing the name and value of the rule.
     *
     * @param value Value of the rule
     */
    public String getName(int value) {
        if (this == FINAL_CARD_RANDOM) return name;
        if (this == KEEPERS_SECRET) {
            return switch (value) {
                case -1 -> "All keepers hidden";
                case 0 -> "No secret keepers";
                case 1 -> "One secret keeper";
                default -> throw new IllegalStateException("Unexpected hidden keeper value, should be in range [-1, 1]: " + value);
            };
        }
        if (this == BONUS) {
            return Bonus.getById(value).name;
        }

        return name + " " + value;
    }

    /**
     * Returns a string containing a sentence explaining what has happened when the rule has changed.
     *
     * @param value The new value of the rule.
     */
    public String getNewValue(int value) {
        if (this == FINAL_CARD_RANDOM)
            return value == 1 ? "The final card is now chosen randomly" : "The final card is no longer chosen randomly";
        if (this == KEEPERS_SECRET) {
            return switch (value) {
                case -1 -> "All keepers are now hidden";
                case 0 -> "All keepers are revealed";
                case 1 -> "Each player may now have one keeper hidden. Choose your hidden keeper with /hide";
                default -> throw new IllegalStateException("Unexpected value, should be in range [-1, 1]: " + value);
            };
        }
        if (this == BONUS) {
            Bonus bonus = Bonus.getById(value);
            return "If a player has " + bonus.keeper.getEmoteAndName() + " visible, they may " + bonus.rule.getActionName("1 extra");
        }

        return "The " + name + " is now " + value;
    }

    enum DisplayStyle {
        ALWAYS, WHEN_NOT_DEFAULT, ONLY_NAME_AND_WHEN_NOT_DEFAULT, CUSTOM
    }

    public String getActionName(String value) {
        String name = getActionNameSingular(value);
        boolean plural = false;
        if (value.length() == 1) plural = value.charAt(0) == '1';
        if (value.length() == 2) {
            if (value.charAt(0) == '1') {
                char second = value.charAt(1);
                if (second < '1' || second > '9') plural = true;
            }
        }

        if (plural) return name + "s";
        return name;
    }

    public String getActionNameSingular(String value) {
        return switch (this) {
            case PLAY_COUNT -> "play " + value + " card";
            case DRAW_COUNT -> "draw " + value + " card";
            case HAND_LIMIT -> "have " + value + " hand card";
            case KEEPER_LIMIT -> "have " + value + " keeper";
            default -> ""; // Only used for bonuses
        };
    }

}
