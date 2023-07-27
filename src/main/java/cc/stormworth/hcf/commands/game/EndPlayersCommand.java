package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EndPlayersCommand {
    @Command(names = {"endplayers"}, permission = "KING_PLUS", async = true)
    public static void endplayers(final Player sender) {
        List<Player> endplayers = new ArrayList<>();

        for (final Player players : Bukkit.getWorld("world_the_end").getPlayers()) {
            if (!players.hasPotionEffect(PotionEffectType.INVISIBILITY) && !players.hasMetadata("invisible")) {
                endplayers.add(players);
            }
        }
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&6End Players&7: &e" + endplayers.size()));
        sender.sendMessage(CC.translate(" "));
    }
}