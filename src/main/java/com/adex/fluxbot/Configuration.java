package com.adex.fluxbot;

import com.google.gson.JsonObject;

public class Configuration {

    private final String botToken;

    public Configuration(JsonObject json) {
        if (!json.has("bot_token")) throw new IllegalArgumentException("Config json missing bot_token");
        botToken = json.get("bot_token").getAsString();
    }

    public String getBotToken() {
        return botToken;
    }
}
