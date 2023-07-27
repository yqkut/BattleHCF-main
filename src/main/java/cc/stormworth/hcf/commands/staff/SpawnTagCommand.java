package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.server.SpawnTagHandler;
import org.bukkit.entity.Player;

public class SpawnTagCommand {
    @Command(names = {"spawntag add"}, permission = "op")
    public static void spawnTagMe(final Player sender) {
        SpawnTagHandler.addOffensiveSeconds(sender, SpawnTagHandler.getMaxTagTime());
    }

    @Command(names = {"spawntag remove"}, permission = "op")
    public static void spawnTagRemove(final Player sender) {
        SpawnTagHandler.removeTag(sender);
    }
}