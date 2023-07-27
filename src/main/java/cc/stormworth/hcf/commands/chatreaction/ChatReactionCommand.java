package cc.stormworth.hcf.commands.chatreaction;

import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatReactionCommand {

    @Command(names = "chatreaction editrewards", permission = "op")
    public static void editRewards(Player player) {
        player.openInventory(Bukkit.createInventory(null, 9 * 6, "Edit Rewards of ChatReaction"));
    }

}
