package com.adex.fluxbot.discord;

import com.adex.fluxbot.discord.listeners.AutoCompleteListener;
import com.adex.fluxbot.discord.listeners.CommandListener;
import com.adex.fluxbot.game.GameManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FluxBot {

    private final JDA jda;

    private final Logger logger;

    private final GameManager gameManager;

    private final CommandListener commandListener;
    private final AutoCompleteListener autoCompleteListener;

    private final Random random;

    public FluxBot(String token) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        logger = LoggerFactory.getLogger(FluxBot.class);
        logger.info("Starting Discord bot");

        random = ThreadLocalRandom.current();

        gameManager = new GameManager(0);

        commandListener = new CommandListener(this);
        commandListener.initCommands();

        autoCompleteListener = new AutoCompleteListener(this);
        autoCompleteListener.initRules();

        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("FluxGame"))
                .addEventListeners(commandListener, autoCompleteListener)
                .build()
                .awaitReady();

        long botOnlineTime = System.currentTimeMillis();
        logger.info("Connected to Discord in {}ms", botOnlineTime - startTime);

        jda.setAutoReconnect(true);

        commandListener.registerCommands(jda);
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

    public GameManager getGameManager() {
        return gameManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public Random getRandom() {
        return random;
    }
}
