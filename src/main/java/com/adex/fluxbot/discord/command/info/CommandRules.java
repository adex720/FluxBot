package com.adex.fluxbot.discord.command.info;

import com.adex.fluxbot.discord.FluxBot;
import com.adex.fluxbot.discord.button.ButtonManager;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Arrays;

public class CommandRules extends Command {

    public static final String BUTTON_ID = "rules";

    public static final MessageEmbed[] PAGES = new MessageEmbed[]{
            new EmbedBuilder().setTitle("Rules").build(),
            new EmbedBuilder().setTitle("Rules").build(),
            new EmbedBuilder().setTitle("Rules").build(),
            new EmbedBuilder().setTitle("Rules").build(),
            new EmbedBuilder().setTitle("Rules").build(),
            new EmbedBuilder().setTitle("Rules").build(),
            new EmbedBuilder().setTitle("Rules").build()
    };

    public static final String[] EMOJI_NAMES = {":track_previous:", ":arrow_left:", ":arrow_right:", ":track_next:"};

    public CommandRules() {
        super("rules", "Read the rules of flux");
    }

    @Override
    public void onInit(FluxBot bot) {
        super.onInit(bot);

        bot.getButtonListener().addButtonManager(new RulesPageManager());
    }

    @Override
    public void execute(EventContext context) {
        OptionMapping optionMapping = context.getOption("page");
        int page = optionMapping != null ? optionMapping.getAsInt() : 1;
        displayPage(context, page);
    }

    public void displayPage(EventContext context, int page) {
        if (context.isFromCommand()) {
            context.getSlashCommandEvent().replyEmbeds(PAGES[page]).addActionRow(getButtons(page)).queue();
        } else {
            var a = getButtons(page);
            System.out.println(Arrays.toString(a));
            context.getButtonEvent()
                    .editMessageEmbeds(PAGES[page - 1])
                    .setActionRow(getButtons(page))
                    .queue();
        }
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "page", "Page of rules", false)
                .setMinValue(1).setMaxValue(PAGES.length)};
    }

    public static String getButtonId(int page) {
        return BUTTON_ID + "-" + page;
    }

    public static String getButtonId(String page) {
        return BUTTON_ID + "-" + page;
    }

    public ItemComponent[] getButtons(int page) {

        // Default case
        if (page > 2 && page < PAGES.length - 1) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId(page - 1), EMOJI_NAMES[1]),
                    Button.primary(getButtonId(page + 1), EMOJI_NAMES[2]),
                    Button.primary(getButtonId(PAGES.length), EMOJI_NAMES[3])};
        }

        if (page == 1) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1) + "-disabled", EMOJI_NAMES[0]).asDisabled(),
                    Button.primary(getButtonId(1) + "-disabled2", EMOJI_NAMES[1]).asDisabled(),
                    Button.primary(getButtonId(3), EMOJI_NAMES[2]),
                    Button.primary(getButtonId(PAGES.length), EMOJI_NAMES[3])};
        }

        if (page == 2) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId("01"), EMOJI_NAMES[1]), // 2 buttons on same message can't have same id
                    Button.primary(getButtonId(page + 1), EMOJI_NAMES[2]),
                    Button.primary(getButtonId(PAGES.length), EMOJI_NAMES[3])};
        }

        if (page == PAGES.length) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId(page - 1), EMOJI_NAMES[1]),
                    Button.primary(getButtonId(PAGES.length) + "-disabled", EMOJI_NAMES[2]).asDisabled(),
                    Button.primary(getButtonId(PAGES.length) + "-disabled2", EMOJI_NAMES[3]).asDisabled()};
        }

        if (page == PAGES.length - 1) {
            return new ItemComponent[]{
                    Button.primary(getButtonId(1), EMOJI_NAMES[0]),
                    Button.primary(getButtonId(page - 1), EMOJI_NAMES[1]),
                    Button.primary(getButtonId("0" + PAGES.length) + "-disabled", EMOJI_NAMES[2]),
                    Button.primary(getButtonId(PAGES.length) + "-disabled2", EMOJI_NAMES[3])};
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
