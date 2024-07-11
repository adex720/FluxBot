package com.adex.fluxbot.discord.command.info;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.button.ButtonManager;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class CommandRules extends Command {

    public static final String BUTTON_ID = "rules";

    private String[] pages;

    public static final Emoji[] EMOJI_NAMES = {Emoji.fromFormatted("U+23EE"),
            Emoji.fromFormatted("U+25C0"),
            Emoji.fromFormatted("U+25B6"),
            Emoji.fromFormatted("U+23EF")};

    public CommandRules() {
        super("rules", "Read the rules of flux");

        pages = new String[]{"Rules not loaded yet"};
    }

    private void leadPages(JsonArray json) {
        int pageCount = json.size();
        pages = new String[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pages[i] = json.get(i).getAsString();
        }
    }

    @Override
    public void onInit(FluxBot bot) {
        super.onInit(bot);

        bot.getButtonListener().addButtonManager(new RulesPageManager());

        try {
            leadPages(bot.getResourceLoader().getResourceJsonArray("rules.json"));
        } catch (FileNotFoundException e) {
            bot.getLogger().error("Failed to read rules from json file: {}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            pages = new String[]{"Failed to load rules"};
        }
    }

    @Override
    public void execute(EventContext context) {
        OptionMapping optionMapping = context.getOption("page");
        int page = optionMapping != null ? optionMapping.getAsInt() : 1;
        displayPage(context, page);
    }

    public void displayPage(EventContext context, int page) {
        if (context.isFromCommand()) {
            context.getSlashCommandEvent().replyEmbeds(MessageCreator.createDefault("Rules",
                            new MessageEmbed.Field("Page " + page + "/" + pages.length, pages[page], false)))
                    .addActionRow(getButtons(page)).queue();
        } else {
            context.getButtonEvent().editMessageEmbeds(MessageCreator.createDefault("Rules",
                            new MessageEmbed.Field("Page " + page + "/" + pages.length, pages[page], false)))
                    .setActionRow(getButtons(page)).queue();
        }
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "page", "Page of rules", false)
                .setMinValue(1).setMaxValue(pages.length)};
    }

    public static String getButtonId(int page) {
        return BUTTON_ID + "-" + page;
    }

    public static String getButtonId(String page) {
        return BUTTON_ID + "-" + page;
    }

    public ItemComponent[] getButtons(int page) {

        // Default case
        if (page > 2 && page < pages.length - 1) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId(page - 1), EMOJI_NAMES[1]),
                    Button.primary(getButtonId(page + 1), EMOJI_NAMES[2]),
                    Button.primary(getButtonId(pages.length), EMOJI_NAMES[3])};
        }

        if (page == 1) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1) + "-disabled", EMOJI_NAMES[0]).asDisabled(),
                    Button.primary(getButtonId(1) + "-disabled2", EMOJI_NAMES[1]).asDisabled(),
                    Button.primary(getButtonId(3), EMOJI_NAMES[2]),
                    Button.primary(getButtonId(pages.length), EMOJI_NAMES[3])};
        }

        if (page == 2) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId("01"), EMOJI_NAMES[1]), // 2 buttons on same message can't have same id
                    Button.primary(getButtonId(page + 1), EMOJI_NAMES[2]),
                    Button.primary(getButtonId(pages.length), EMOJI_NAMES[3])};
        }

        if (page == pages.length) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId(page - 1), EMOJI_NAMES[1]),
                    Button.primary(getButtonId(pages.length) + "-disabled", EMOJI_NAMES[2]).asDisabled(),
                    Button.primary(getButtonId(pages.length) + "-disabled2", EMOJI_NAMES[3]).asDisabled()};
        }

        if (page == pages.length - 1) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId(page - 1), EMOJI_NAMES[1]),
                    Button.primary(getButtonId("0" + pages.length) + "-disabled", EMOJI_NAMES[2]),
                    Button.primary(getButtonId(pages.length) + "-disabled2", EMOJI_NAMES[3])};
        }

        throw new IllegalStateException("This should never be reached");
    }

    public class RulesPageManager extends ButtonManager {

        public RulesPageManager() {
            super(BUTTON_ID);
        }

        @Override
        public void onButtonPress(EventContext context) {
            displayPage(context, Integer.parseInt(context.getButtonIdSplit()[1]));
        }
    }
}
