package com.adex.fluxbot.discord.command;

import com.adex.fluxbot.discord.command.game.*;

public class Commands {

    public static final Command COMMAND_CREATE = new CommandCreate();
    public static final Command COMMAND_JOIN = new CommandJoin();
    public static final Command COMMAND_LEAVE = new CommandLeave();
    public static final Command COMMAND_START = new CommandStart();
    public static final Command COMMAND_INVITE = new CommandInvite();

    public static final Command COMMAND_PLAY = new CommandPlay();
    public static final Command COMMAND_DISCARD = new CommandDiscard();
    public static final Command COMMAND_REMOVE = new CommandRemove();

}
