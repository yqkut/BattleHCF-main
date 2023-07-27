package cc.stormworth.hcf.team.commands.pvp;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.pvptimer.PvPTimer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPCreateCommand {
    @Command(names = {"pvptimer create", "timer create", "pvp create", "pvp add", "pvp give"}, permission = "op", async = true)
    public static void pvpCreate(final Player sender, @Param(name = "player", defaultValue = "self") final Player player, @Param(name = "time", defaultValue = "30m") final String time) {
        final int seconds = TimeUtils.parseTime(time);
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }

        HCFProfile profile = HCFProfile.get(player);

        profile.setPvpTimer(new PvPTimer(false));

        player.sendMessage(ChatColor.YELLOW + "You have " + time + " of PVP Timer!");
        if (sender != player) {
            sender.sendMessage(ChatColor.YELLOW + "Gave " + time + " of PVP Timer to " + player.getName() + ".");
        }
        CorePlugin.getInstance().getNametagEngine().reloadPlayer(player);
    }
}