package cc.stormworth.hcf.misc.daily.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.daily.menu.DailyMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DailyCommand {

    @Command(names = "daily", permission = "", async = true)
    public static void dailyMenu(Player player) {
        new DailyMenu().open(player);
    }

    @Command(names = {"resetdaily"}, permission = "DEVELOPER")
    public static void resetdaily(Player player, @Param(name = "target") Player target) {
        Main.getInstance().getDailyManager().reset(target);

        player.sendMessage(ChatColor.GREEN + "Reset daily to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "Your daily has been reset by " + player.getName());
    }
}
