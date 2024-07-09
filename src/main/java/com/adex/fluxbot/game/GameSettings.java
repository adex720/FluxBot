package com.adex.fluxbot.game;

import com.adex.fluxbot.Util;

/**
 * Provides settings for a game.
 * Value of all settings is stored in a 32-bit integer.
 * Different bits refer to different settings.
 * When creating a game its settings will be same as in the previous game the creator hosted.
 * When creating a new profile, all settings will use a default value of 0.
 */
public class GameSettings {

    public static final SettingType INVITE_ONLY = new SettingType(1); // Is invite needed when game hasn't started
    public static final SettingType ANYONE_CAN_JOIN_WHEN_ON = new SettingType(1); // Can game be joined without invite when it has started

    private int value;

    public GameSettings(int defaultValue) {
        this.value = defaultValue;
    }

    public int get(SettingType setting) {
        return (value & setting.getMask()) >> setting.offset;
    }

    public void set(SettingType setting, int value) {
        if (value > setting.maxValue) return;
        this.value = this.value & (~setting.getMask()); // Setting value of the setting to 0
        this.value = this.value | (value << setting.offset); // Adding new value
    }

    private static int OFFSET_LENGTH = 0;

    /**
     * Stores information about where in the integer the setting value is stored.
     */
    public static class SettingType {
        public final int offset; // Number of bits to the right of the setting
        public final int length; // Number of bits used to store the value
        public final int maxValue; // Largest allowed value of the setting

        public SettingType(int length) {
            this.offset = OFFSET_LENGTH;
            this.length = length;
            OFFSET_LENGTH += length;
            this.maxValue = Util.intPow(2, length) - 1;
        }

        public SettingType(int length, int maxValue) {
            if (maxValue > Util.intPow(2, length) - 1) {
                throw new IllegalArgumentException("Maximum value for setting is too large compared to length of bits used for storing");
            }

            this.offset = OFFSET_LENGTH;
            this.length = length;
            OFFSET_LENGTH += length;
            this.maxValue = maxValue;
        }

        /**
         * Returns an int with 1 as bits who are part of this setting and 0 elsewhere
         */
        public int getMask() {
            return (Util.intPow(2, length) - 1) << offset;
        }
    }

}
