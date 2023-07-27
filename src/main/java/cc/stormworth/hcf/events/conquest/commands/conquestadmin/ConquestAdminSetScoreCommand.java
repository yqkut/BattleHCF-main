package cc.stormworth.hcf.events.conquest.commands.conquestadmin;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.conquest.ConquestHandler;
import cc.stormworth.hcf.events.conquest.game.ConquestGame;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConquestAdminSetScoreCommand {

    @Command(names = {"conquestadmin setscore"}, permission = "op")
    public static void conquestAdminSetScore(CommandSender sender, @Param(name = "team") Team team, @Param(name = "score") int score) {
        ConquestGame game = Main.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        ConquestGame.getTeamPoints().put(team.getUniqueId(), score);
        sender.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Updated the score for " + team.getName() + ChatColor.GOLD + ".");
    }
}