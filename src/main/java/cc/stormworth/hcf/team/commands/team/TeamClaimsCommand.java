package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.Claim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamClaimsCommand {

    @Command(names = {"team claims", "t claims", "f claims", "faction claims", "fac claims"}, permission = "")
    public static void claims(final Player sender, @Param(name = "team", defaultValue = "self") final Team team) {
        if (!sender.isOp() && !team.isMember(sender.getUniqueId())) {
            sender.sendMessage(CC.RED + "You cannot check others claims.");
            return;
        }
        sender.sendMessage(CC.translate("&eClaims of &6" + team.getName(sender) + " &7(" + team.getClaims().size() + "):"));
        for (final Claim claim : team.getClaims()) {
            sender.sendMessage(ChatColor.GRAY + " " + claim.getFriendlyName());
        }
    }
}