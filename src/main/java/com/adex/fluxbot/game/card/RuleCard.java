package com.adex.fluxbot.game.card;

import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.rule.Rule;

/**
 * A card which sets a rule to a specific value.
 */
public class RuleCard extends Card {

    public RuleCard(String name, Rule rule, int value) {
        super(name, Type.RULE);
        this.rule = rule;
        this.value = value;
    }

    public RuleCard(Rule rule, int value) {
        super(rule.name + " " + value, Type.RULE);
        this.rule = rule;
        this.value = value;
    }

    public final Rule rule;
    public final int value;

    @Override
    public void onPlay(FluxGame game, EventContext context) {
        game.setRule(rule, value);
    }
}
