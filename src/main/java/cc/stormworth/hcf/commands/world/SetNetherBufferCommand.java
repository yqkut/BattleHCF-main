package cc.stormworth.hcf.commands.world;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetNetherBufferCommand {
    @Command(names = {"NetherBuffer set"}, permission = "op", async = true)
    public static void setNetherBuffer(final Player sender, @Param(name = "netherBuffer") final int newBuffer) {
        Main.getInstance().getMapHandler().setNetherBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The nether buffer is now set to " + newBuffer + " blocks.");
        Main.getInstance().getMapHandler().saveNetherBuffer();
    }
}