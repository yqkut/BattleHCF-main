package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class NetherPlayersCommand {
    @Command(names = {"netherplayers"}, permission = "KING_PLUS", async = true)
    public static void netherplayers(final Player sender) {
        List<Player> netherplayers = new ArrayList<>();
        for (final Player players : Bukkit.getWorld("world_nether").getPlayers()) {
            if (!players.hasPotionEffect(PotionEffectType.INVISIBILITY) && !players.hasMetadata("invisible")) {
                netherplayers.add(players);
            }
        }
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&cNether Players&7: &e" + netherplayers.size()));
        sender.sendMessage(CC.translate(" "));
    }
}