package cc.stormworth.hcf.misc.lunarclient.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.misc.lunarclient.waypoint.PlayerWaypointType;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import org.bukkit.entity.Player;

public class WaypointsCommand {

    @Command(names = "waypoint move", permission = "op")
    public static void move(Player player, @Param(name = "name") String name) {
        PlayerWaypointType type = PlayerWaypointType.getByName(name);

        if (type == null) {
            player.sendMessage("Invalid waypoint type.");
            player.sendMessage("Valid waypoint types: ");
            for (PlayerWaypointType t : PlayerWaypointType.values()) {
                player.sendMessage(t.name());
            }
            return;
        }

        WaypointManager.addGlobalWaypoint(type, player.getLocation());

        player.sendMessage(CC.translate("&aWaypoint &7" + name + " &ahas been set to your current location."));
    }

}
