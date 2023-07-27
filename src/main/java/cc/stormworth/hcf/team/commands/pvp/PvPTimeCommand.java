package cc.stormworth.hcf.team.commands.pvp;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPTimeCommand {
    @Command(names = {"pvptimer time", "timer time", "pvp time"}, permission = "", async = true)
    public static void pvpTime(final Player sender) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }

        HCFProfile hcfProfile = HCFProfile.get(sender);

        if (hcfProfile.hasPvPTimer()) {
            sender.sendMessage(ChatColor.RED + "You have " + ScoreFunction.TIME_FANCY.apply((hcfProfile.getPvpTimer().getRemaining() / 1000F)) + " left on your PVP Timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have a PVP Timer on!");
        }
    }
}