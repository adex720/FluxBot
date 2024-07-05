package com.adex.fluxbot.game.card;

import com.adex.fluxbot.game.Flux;
import com.adex.fluxbot.game.Rule;

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
    public void onPlay(Flux game) {
        game.ruleset.set(rule, value);
    }
}
