package com.adex.fluxbot;

import com.adex.fluxbot.discord.FluxBot;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Configuration config;
        FluxBot bot;
        try {
            config = new Configuration(getConfigJson());
            bot = new FluxBot(config.getBotToken());
        } catch (FileNotFoundException e) {
            System.out.println("Failed to find configuration json file!");
            return;
        } catch (InterruptedException | IllegalStateException e) {
            System.out.println("Failed to start Discord bot!\n" + e.getMessage() + Arrays.toString(e.getStackTrace()));
            return;
        }

    }

    public static JsonObject getConfigJson() throws FileNotFoundException {
        return JsonParser.parseReader(new FileReader("config.json")).getAsJsonObject();
    }
}
