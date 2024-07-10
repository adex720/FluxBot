package com.adex.fluxbot.discord;

import com.adex.fluxbot.discord.listeners.AutoCompleteListener;
import com.adex.fluxbot.discord.listeners.ButtonListener;
import com.adex.fluxbot.discord.listeners.CommandListener;
import com.adex.fluxbot.file.ResourceLoader;
import com.adex.fluxbot.game.GameManager;
import com.adex.fluxbot.game.card.Cards;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FluxBot {

    private final JDA jda;

    private final Logger logger;

    private final ResourceLoader resourceLoader;

    private final GameManager gameManager;

    private final CommandListener commandListener;
    private final AutoCompleteListener autoCompleteListener;
    private final ButtonListener buttonListener;

    private final Random random;

    private final JsonObject linkJson;

    public FluxBot(String token) throws InterruptedException, IllegalStateException {
        logger = LoggerFactory.getLogger(FluxBot.class);
        resourceLoader = new ResourceLoader();

        // loading resources
        logger.info("Starting to load resources");
        long startTime = System.currentTimeMillis();

        try {
            linkJson = resourceLoader.getResourceJsonObject("links.json");
        } catch (FileNotFoundException e) {
            long endTime = System.currentTimeMillis();
            logger.info("Failed to load resources, took {}ms: {}\n{}", endTime - startTime, e.getMessage(), Arrays.toString(e.getStackTrace()));
            throw new IllegalStateException("Trying to load a non-existing file");
        }

        long endTime = System.currentTimeMillis();
        logger.info("Loaded resources in {}ms", endTime - startTime);

        // Starting bot
        startTime = System.currentTimeMillis();
        logger.info("Starting Discord bot");

        random = ThreadLocalRandom.current();

        gameManager = new GameManager(0);

        commandListener = new CommandListener(this);
        autoCompleteListener = new AutoCompleteListener(this);
        buttonListener = new ButtonListener(this); // Has to be defined before registering commands

        commandListener.initCommands();
        autoCompleteListener.initRules();

        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("FluxGame"))
                .addEventListeners(commandListener, autoCompleteListener, buttonListener)
                .build()
                .awaitReady();

        long botOnlineTime = System.currentTimeMillis();
        logger.info("Connected to Discord in {}ms", botOnlineTime - startTime);

        jda.setAutoReconnect(true);

        commandListener.registerCommands(jda);

        logger.info("Loaded {} cards", Cards.getCardAmount());
    }

    public JDA getJda() {
        return jda;
    }

    public CommandListener getCommandListener() {
        return commandListener;
    }

    public AutoCompleteListener getAutoCompleteListener() {
        return autoCompleteListener;
    }

    public ButtonListener getButtonListener() {
        return buttonListener;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public Random getRandom() {
        return random;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public JsonObject getLinkJson() {
        return linkJson;
    }

    /**
     * Returns the value matching the given key at links.json resource file.
     *
     * @param name Name of the link
     * @return link as String
     * @throws UnsupportedOperationException if the key doesn't match to a String.
     */
    public String getLink(String name) {
        return linkJson.get(name).getAsString();
    }
}
