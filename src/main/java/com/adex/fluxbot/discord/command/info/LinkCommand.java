package com.adex.fluxbot.discord.command.info;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class LinkCommand extends Command {

    private String link;
    public final String linkName;
    public final String buttonText;
    private final LinkFetcher linkFetcher;

    /**
     * Creates a command which sends a message including a link button.
     *
     * @param name        Name of the command in Discord
     * @param description Description of the command in Discord
     * @param link        Where to take the user
     * @param linkName    Name of the destination of the link
     * @param buttonText  Text on the button
     */
    public LinkCommand(String name, String description, String link, String linkName, String buttonText) {
        super(name, description);
        this.link = link;
        this.linkName = linkName;
        this.buttonText = buttonText;
        this.linkFetcher = null;
    }

    /**
     * Creates a command which sends a message including a link button.
     *
     * @param name        Name of the command in Discord
     * @param description Description of the command in Discord
     * @param link        Where to take the user
     * @param linkName    Name of the destination of the link, will also be used as text on the button
     */
    public LinkCommand(String name, String description, String link, String linkName) {
        super(name, description);
        this.link = link;
        this.linkName = linkName;
        this.buttonText = linkName;
        this.linkFetcher = null;
    }

    /**
     * Creates a command which sends a message including a link button.
     *
     * @param name        Name of the command in Discord
     * @param description Description of the command in Discord
     * @param linkFetcher LinkFetcher
     * @param linkName    Name of the destination of the link
     * @param buttonText  Text on the button
     */
    public LinkCommand(String name, String description, LinkFetcher linkFetcher, String linkName, String buttonText) {
        super(name, description);
        this.link = null;
        this.linkName = linkName;
        this.buttonText = buttonText;
        this.linkFetcher = linkFetcher;
    }

    /**
     * Creates a command which sends a message including a link button.
     *
     * @param name        Name of the command in Discord
     * @param description Description of the command in Discord
     * @param linkFetcher LinkFetcher
     * @param linkName    Name of the destination of the link, will also be used as text on the button
     */
    public LinkCommand(String name, String description, LinkFetcher linkFetcher, String linkName) {
        super(name, description);
        this.link = null;
        this.linkName = linkName;
        this.buttonText = linkName;
        this.linkFetcher = linkFetcher;
    }

    @Override
    public void onInit(FluxBot bot) {
        if (linkFetcher != null) this.link = linkFetcher.getLink(bot);
    }

    @Override
    public void execute(EventContext context) {
        context.getSlashCommandEvent().replyEmbeds(MessageCreator.createDefault("Press the button to go to the " + linkName))
                .addActionRow(Button.link(link, buttonText)).queue();
    }

    public interface LinkFetcher {
        String getLink(FluxBot bot);

        static LinkFetcher fromJson(String name) {
            return bot -> bot.getLink(name);
        }
    }
}
