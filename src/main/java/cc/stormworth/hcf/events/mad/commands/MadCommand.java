package cc.stormworth.hcf.events.mad.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.mad.MadGame;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MadCommand {

    @Command(names = "mad start", permission = "op")
    public static void start(Player player){

        if(MadGame.isStarted()){
            player.sendMessage(CC.translate("&6&lMad Event &cThe game has already started!"));
            return;
        }

        Main.getInstance().getEventHandler().setScheduleEnabled(false);
        MadGame.start();
    }

    @Command(names = {"mad end", "mad stop"}, permission = "op")
    public static void end(Player player){

        if(!MadGame.isStarted()){
            player.sendMessage(CC.translate("&6&lMad Event &cThe game has not started yet!"));
            return;
        }

        MadGame.endGame(null, null);
    }

    @Command(names = {"madadmin setscore"}, permission = "op")
    public static void conquestAdminSetScore(CommandSender sender, @Param(name = "team") Team team, @Param(name = "score") int score) {
        if (MadGame.isStarted()) {
            sender.sendMessage(ChatColor.RED + "Mad is not active.");
            return;
        }

        MadGame.getTeamPoints().put(team.getUniqueId(), score);
        sender.sendMessage(MadGame.PREFIX + " " + ChatColor.GOLD + "Updated the score for " + team.getName() + ChatColor.GOLD + ".");
    }

}
