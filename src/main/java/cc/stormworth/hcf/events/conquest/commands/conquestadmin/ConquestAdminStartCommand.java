package cc.stormworth.hcf.events.conquest.commands.conquestadmin;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.conquest.game.ConquestGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConquestAdminStartCommand {

    @Command(names = {"conquestadmin start"}, permission = "op")
    public static void conquestAdminStart(CommandSender sender) {
        ConquestGame game = Main.getInstance().getConquestHandler().getGame();

        if (game != null) {
            if (sender instanceof Player) sender.sendMessage(ChatColor.RED + "Conquest is already active.");
            return;
        }

        Main.getInstance().getEventHandler().setScheduleEnabled(false);
        new ConquestGame();
    }
}