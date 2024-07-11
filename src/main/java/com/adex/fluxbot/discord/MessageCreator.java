package com.adex.fluxbot.discord;

import com.adex.fluxbot.game.card.Card;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Arrays;
import java.util.Date;

public class MessageCreator {

    public static final int DEFAULT_COLOR = Card.Type.RULE.color;

    public static final int ERROR_COLOR = 0x8c0101;

    public static final String COMMAND_TIPS = """
            Use /hand to view your cards
            Use /keepers to view your keepers
            Use /table to view everyone's keepers and hand size
            Use /play to play a card
            """; // TODO: replace with command mentions

    public static MessageEmbed createDefault(String title, MessageEmbed.Field... fields) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(title)
                .setColor(DEFAULT_COLOR)
                .setTimestamp(new Date().toInstant());
        for (MessageEmbed.Field field : fields) {
            embedBuilder.addField(field);
        }
        return embedBuilder.build();
    }

    public static MessageEmbed createDefault(String title, Card.Type type, MessageEmbed.Field... fields) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(title)
                .setColor(type.color)
                .setTimestamp(new Date().toInstant());
        for (MessageEmbed.Field field : fields) {
            embedBuilder.addField(field);
        }
        return embedBuilder.build();
    }

    public static MessageEmbed createDefault(String title, int color, MessageEmbed.Field... fields) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(title)
                .setColor(color)
                .setTimestamp(new Date().toInstant());
        for (MessageEmbed.Field field : fields) {
            embedBuilder.addField(field);
        }
        return embedBuilder.build();
    }

    public static MessageEmbed createDefault(String title, String fieldTitle, String fieldContent) {
        return createDefault(title, new MessageEmbed.Field(fieldTitle, fieldContent, true));
    }

    public static MessageEmbed createDefault(String title, Card.Type type, String fieldTitle, String fieldContent) {
        return createDefault(title, type, new MessageEmbed.Field(fieldTitle, fieldContent, true));
    }

    public static MessageEmbed createDefault(String title, int color, String fieldTitle, String fieldContent) {
        return createDefault(title, color, new MessageEmbed.Field(fieldTitle, fieldContent, true));
    }

    public static MessageEmbed createErrorMessage(Exception e) {
        String stackTrace = Arrays.toString(e.getStackTrace());
        if (stackTrace.length() > 1024) stackTrace = stackTrace.substring(0, 1020) + "...";
        return createDefault("Exception", ERROR_COLOR, e.getMessage(), stackTrace);
    }


}
