package com.adex.fluxbot;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.file.ResourceLoader;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        ResourceLoader resourceLoader = new ResourceLoader();
        Configuration config;
        FluxBot bot;
        try {
            config = new Configuration(resourceLoader.getConfigJson());
            bot = new FluxBot(config.getBotToken());
        } catch (FileNotFoundException e) {
            System.out.println("Failed to find configuration json file!");
            return;
        } catch (InterruptedException e) {
            System.out.println("Failed to start Discord bot!\n" + e.getMessage() + Arrays.toString(e.getStackTrace()));
            return;
        }


    }
}
