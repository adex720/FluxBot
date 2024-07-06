package com.adex.fluxbot.game.card;

import com.adex.fluxbot.game.Flux;
import com.adex.fluxbot.game.keeper.Keeper;

public class KeeperCard extends Card {

    public final Keeper keeper;

    public KeeperCard(Keeper keeper) {
        super(keeper.getEmoteAndName(), Type.KEEPER);
        this.keeper = keeper;
    }

    @Override
    public void onPlay(Flux game) {
        game.currentPlayer().getKeepers().add(keeper);
    }
}
