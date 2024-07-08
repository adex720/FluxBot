package com.adex.fluxbot.discord.command;

import com.adex.fluxbot.discord.command.game.CommandDiscard;
import com.adex.fluxbot.discord.command.game.CommandPlay;

public class Commands {

    public static final Command COMMAND_PLAY = new CommandPlay();
    public static final Command COMMAND_DISCARD = new CommandDiscard();

}
