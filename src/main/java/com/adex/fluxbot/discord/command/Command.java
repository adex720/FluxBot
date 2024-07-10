package com.adex.fluxbot.discord.command;

import com.adex.fluxbot.discord.FluxBot;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public abstract class Command {

    public final String name;
    public final String description;
    protected int id;

    protected Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets run when the command is being registered
     *
     * @param bot Flux bot
     */
    public void onInit(FluxBot bot) {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public abstract void execute(EventContext context);

    public OptionData[] getOptionData() {
        return new OptionData[0];
    }

    public CommandData getCommandData() {
        return new CommandDataImpl(name, description).addOptions(getOptionData());
    }

    public String getName() {
        return name;
    }
}
