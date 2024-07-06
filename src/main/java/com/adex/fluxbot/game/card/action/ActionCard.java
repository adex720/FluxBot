package com.adex.fluxbot.game.card.action;

import com.adex.fluxbot.game.card.Card;

public abstract class ActionCard extends Card {

    public ActionCard(String name) {
        super(name, Type.ACTION);
    }
}
