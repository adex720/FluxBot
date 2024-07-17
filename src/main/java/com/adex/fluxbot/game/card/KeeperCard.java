package com.adex.fluxbot.game.card;

import com.adex.fluxbot.Util;
import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.keeper.Keeper;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class KeeperCard extends Card {

    public final Keeper keeper;

    public KeeperCard(Keeper keeper) {
        super(keeper.name, Type.KEEPER);
        this.keeper = keeper;
        this.keeper.setCard(this);
    }

    @Override
    public void onPlay(FluxGame game, EventContext context) {
        Player player = game.currentPlayer();
        player.getKeepers().add(keeper);
        game.keepersChanged();

        context.getSlashCommandEvent().replyEmbeds(
                getPlayMessage(context.getUsername(), player.isKeeperHidden(keeper), player.getKeeperCount())).queue();
    }

    @Override
    public String getEmote() {
        return keeper.getEmote();
    }

    public MessageEmbed getPlayMessage(String username, boolean hidden, int keeperCount) {
        String description = "They now have " + Util.combineCountAndWord(keeperCount, "keeper");
        if (!hidden) return getPlayMessage(username, description);
        return MessageCreator.createDefault("Card played", type, username + " played a keeper", description);
    }
}
