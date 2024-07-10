package com.adex.fluxbot.discord.command;

import com.adex.fluxbot.discord.command.game.*;
import com.adex.fluxbot.discord.command.info.CommandRules;
import com.adex.fluxbot.discord.command.info.LinkCommand;

public class Commands {

    public static final Command COMMAND_GITHUB = new LinkCommand("github", "Sends a link the the GitHub page", LinkCommand.LinkFetcher.fromJson("github"), "GitHub page", "GitHub");
    public static final Command COMMAND_INVITE_LINK = new LinkCommand("invite-link", "Invite the bot to your server", LinkCommand.LinkFetcher.fromJson("invite"), "bot invite page", "Invite");
    public static final Command COMMAND_SERVER = new LinkCommand("server", "Join the official server", LinkCommand.LinkFetcher.fromJson("server"), "official server", "Join");

    public static final Command COMMAND_RULES = new CommandRules();

    public static final Command COMMAND_CREATE = new CommandCreate();
    public static final Command COMMAND_JOIN = new CommandJoin();
    public static final Command COMMAND_LEAVE = new CommandLeave();
    public static final Command COMMAND_START = new CommandStart();
    public static final Command COMMAND_INVITE = new CommandInvite();

    public static final Command COMMAND_PLAY = new CommandPlay();
    public static final Command COMMAND_DISCARD = new CommandDiscard();
    public static final Command COMMAND_REMOVE = new CommandRemove();

}
