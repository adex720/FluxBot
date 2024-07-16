package com.adex.fluxbot.discord.command.game;

import com.adex.fluxbot.discord.MessageCreator;
import com.adex.fluxbot.discord.command.Command;
import com.adex.fluxbot.discord.command.EventContext;
import com.adex.fluxbot.game.FluxGame;
import com.adex.fluxbot.game.Player;
import com.adex.fluxbot.game.keeper.Keeper;
import com.adex.fluxbot.game.rule.Rule;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandHide extends Command {

    public CommandHide() {
        super("hide", "Chooses a keeper to hide");
    }

    @Override
    public void execute(EventContext context) {
        if (!context.inGame()) {
            reply(context, MessageCreator.createDefault("Hide", "Cannot hide keeper", "You are not in a game"), true);
            return;
        }

        FluxGame game = context.getGame();
        if (!game.hasStarted()) {
            reply(context, MessageCreator.createDefault("Hide", "Cannot hide keeper", "The game hasn't started yet"), true);
            return;
        }

        if (game.getRule(Rule.KEEPERS_SECRET) != 1) {
            reply(context, MessageCreator.createDefault("Hide", "Cannot hide keeper", "All keepers are currently visible or hidden"), true);
            return;
        }

        long userId = context.getUserId();
        Player player = game.getPlayerByUserId(userId);

        if (player.getKeeperCount() == 0) {
            reply(context, MessageCreator.createDefault("Hide", "Cannot hide keeper", "You don't have any keepers"), true);
            return;
        }

        int keeperId = context.getOption("keeper").getAsInt();
        if (!player.hasKeeper(keeperId)) {
            reply(context, MessageCreator.createDefault("Hide", "Cannot hide keeper", "You can only hide keepers you have in front of you"), true);
            return;
        }

        Keeper keeper = Keeper.getKeeperById(keeperId);
        if (keeper == null) {
            reply(context, MessageCreator.createDefault("Hide", "Cannot hide keeper", "You can only hide keepers you have in front of you"), true);
            return;
        }

        player.hideKeeper(keeper);
        game.checkBonus();
        reply(context, MessageCreator.createDefault("Hide", "Successfully changed your hidden keeper",
                "Your hidden keeper is now " + keeper.getEmoteAndName()), true);
    }

    @Override
    public OptionData[] getOptionData() {
        return new OptionData[]{new OptionData(OptionType.INTEGER, "keeper", "Keeper to hide", true, true)};
    }

}
