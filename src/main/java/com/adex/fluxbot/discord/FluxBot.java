package com.adex.fluxbot.discord;

import com.adex.fluxbot.discord.listeners.CommandListener;
import com.adex.fluxbot.game.GameManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluxBot {

    private final JDA jda;

    private final Logger logger;

    public final GameManager gameManager;

    public final CommandListener commandListener;

    public FluxBot(String token) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        logger = LoggerFactory.getLogger(FluxBot.class);
        logger.info("Starting Discord bot");

        gameManager = new GameManager();

        commandListener = new CommandListener(this);
        commandListener.initCommands();

        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("FluxGame"))
                .addEventListeners()
                .build()
                .awaitReady();

        long botOnlineTime = System.currentTimeMillis();
        logger.info("Connected to Discord in {}ms", botOnlineTime - startTime);

        jda.setAutoReconnect(true);

        commandListener.registerCommands(jda);
    }

    public Logger getLogger() {
        return logger;
    }
}
