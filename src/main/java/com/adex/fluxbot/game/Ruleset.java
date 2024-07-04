package com.adex.fluxbot.game;

/**
 * Contains the values of each rule in a game
 */
public class Ruleset {

    private final int[] values;

    public Ruleset() {
        this.values = getDefaultValues();
    }

    /**
     * Returns the current value of the rule.
     *
     * @param rule Rule
     */
    public int get(Rule rule) {
        return values[rule.id];
    }

    /**
     * Returns the current value of the rule.
     *
     * @param ruleId Id of the rule
     */
    public int get(int ruleId) {
        return values[ruleId];
    }

    /**
     * Sets the value of the rule into the given value.
     * Checks if the value is in the allowed range.
     *
     * @param rule  Rule
     * @param value New value
     */
    public void set(Rule rule, int value) {
        if (value < rule.min || value > rule.max) {
            System.out.println("Invalid value: " + value + " for rule " + rule.name);
            return;
        }

        values[rule.id] = value;
    }

    /**
     * Sets the value of the rule into the given value.
     * Checks if the value is in the allowed range.
     *
     * @param ruleId Id of the rule
     * @param value  New value
     */
    public void set(int ruleId, int value) {
        set(Rule.values()[ruleId], value);
    }

    /**
     * Sets the value of the rule into its default value.
     *
     * @param rule Rule
     */
    public void reset(Rule rule) {
        values[rule.id] = rule.defaultValue;
    }

    /**
     * Sets the value of the rule into its default value.
     *
     * @param ruleId Id of the rule
     */
    public void reset(int ruleId) {
        reset(Rule.values()[ruleId]);
    }

    /**
     * Sets the value of each rule into its default value.
     */
    public void resetAll() {
        Rule[] rules = Rule.values();

        for (int i = 0; i < Rule.RULE_AMOUNT; i++) {
            values[i] = rules[i].defaultValue;
        }
    }

    /**
     * Returns an array containing the default values of each rule in the order the rules were declared.
     */
    public static int[] getDefaultValues() {
        int[] values = new int[Rule.RULE_AMOUNT];
        Rule[] rules = Rule.values();

        for (int i = 0; i < Rule.RULE_AMOUNT; i++) {
            values[i] = rules[i].defaultValue;
        }

        return values;
    }
}
