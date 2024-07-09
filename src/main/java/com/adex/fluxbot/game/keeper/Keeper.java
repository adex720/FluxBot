package com.adex.fluxbot.game.keeper;

import com.adex.fluxbot.game.card.KeeperCard;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;

/**
 * Keepers are a type of card played in front of each player.
 */
public class Keeper {

    private static int KEEPER_COUNT;
    private static final ArrayList<Keeper> KEEPERS = new ArrayList<>();

    public final String name;
    public final String emote;
    public final int id;

    private KeeperCard card;

    /**
     * @param name  Name of the keeper.
     * @param emote Name of the emote on Discord and the emote's id, separated by a colon.
     */
    public Keeper(String name, String emote) {
        this.name = name;
        this.emote = emote;
        id = KEEPER_COUNT++;
        KEEPERS.add(this);

        card = null;
    }

    public static Keeper getKeeperById(int id) {
        if (id < 0 || id >= KEEPER_COUNT) return null;
        return KEEPERS.get(id);
    }

    /**
     * Returns the emote of the keeper formatted to display on Discord.
     */
    public String getEmote() {
        return "<:" + emote + ">";
    }

    /**
     * Returns the emote of the keeper formatted to display on Discord followed by its name.
     */
    public String getEmoteAndName() {
        return "<:" + emote + "> " + name;
    }

    /**
     * Returns the name of the emote.
     */
    public String getName() {
        return name;
    }

    public void setCard(KeeperCard card) {
        this.card = card;
    }

    public KeeperCard getCard() {
        return card;
    }

    public Command.Choice getAsChoice() {
        return new Command.Choice(name, id);
    }
}
