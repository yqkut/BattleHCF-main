package cc.stormworth.hcf.commands.world;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SetWorldBufferCommand {

    @Command(names = {"WorldBuffer set"}, permission = "op")
    public static void setWorldBuffer(final Player sender, @Param(name = "worldBuffer") final int newBuffer) {
        Main.getInstance().getMapHandler().setWorldBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The world buffer is now set to " + newBuffer + " blocks.");
        new BukkitRunnable() {
            public void run() {
                Main.getInstance().getMapHandler().saveWorldBuffer();
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}