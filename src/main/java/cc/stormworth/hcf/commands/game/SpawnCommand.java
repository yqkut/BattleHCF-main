package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.cmds.staff.FreezeCommand;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpawnCommand {

    @Command(names = {"spawn"}, permission = "")
    public static void spawn(final Player sender, @Param(name = "player", defaultValue = "self") final Player player) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            if (sender.getGameMode() != GameMode.CREATIVE) {
                if (DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
                    sender.sendMessage(CC.RED + "You cannot use spawn inside a safe-zone.");
                    return;
                }
                if (SpawnTagHandler.isTagged(sender)) {
                    sender.sendMessage(CC.RED + "You cannot teleport while spawn tagged.");
                    return;
                }
                if (FreezeCommand.getFreezes().containsKey(sender.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You cannot teleport while you're frozen!");
                    return;
                }
                if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
                    player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
                    return;
                }
                if (Main.getInstance().getServerHandler().getSpawntasks().containsKey(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "You are already warping.");
                    return;
                }
                Main.getInstance().getServerHandler().startSpawnSequence(sender);
            } else if (sender.hasPermission("core.staff")) {
                player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
            } else {
                sender.sendMessage(ChatColor.RED + "You cannot use that command, Spawn is located at 0, 0.");
            }
        } else if (sender.hasPermission("core.staff") && sender.getGameMode() == GameMode.CREATIVE) {
            player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
        } else {
            if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
                player.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
                return;
            }
            sender.sendMessage(ChatColor.RED + "You cannot use that command, Spawn is located at 0, 0.");
        }
    }
}