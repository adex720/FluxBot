package com.adex.fluxbot.discord.button;

import com.adex.fluxbot.discord.command.EventContext;

public abstract class ButtonManager {

    public final String buttonId;

    public ButtonManager(String buttonId) {
        this.buttonId = buttonId;
    }

    public abstract void onButtonPress(EventContext context);

    public boolean matches(String buttonId) {
        return this.buttonId.equals(buttonId);
    }
}
