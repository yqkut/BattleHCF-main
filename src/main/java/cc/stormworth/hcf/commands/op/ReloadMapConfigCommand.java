package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReloadMapConfigCommand {
    @Command(names = {"configmap reload", "mapconfig reload"}, permission = "op", hidden = true)
    public static void reloadMapConfig(final Player sender) {
        Main.getInstance().getMapHandler().reloadConfig();
        sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded mapInfo.json from file.");
    }
}