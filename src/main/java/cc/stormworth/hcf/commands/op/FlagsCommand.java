package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlagsCommand {
    @Command(names = {"flags list", "flag list"}, hidden = true, permission = "op")
    public static void bitmaskList(final Player sender) {
        for (final DTRBitmask bitmaskType : DTRBitmask.values()) {
            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }
    }

    @Command(names = {"flags info", "flag info"}, hidden = true, permission = "op")
    public static void bitmaskInfo(final Player sender, @Param(name = "team") final Team team) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "Bitmask flags of " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + ":");
        for (final DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (team.hasDTRBitmask(bitmaskType)) {
                sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
            }
        }
        sender.sendMessage(ChatColor.GOLD + "Raw DTR: " + ChatColor.YELLOW + team.getDTR());
    }

    @Command(names = {"flags add", "flag add"}, hidden = true, permission = "op")
    public static void bitmaskAdd(final Player sender, @Param(name = "target") final Team team, @Param(name = "bitmask") final DTRBitmask bitmask) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }
        if (team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim already has the bitmask value " + bitmask.getName() + ".");
            return;
        }
        int dtrInt = (int) team.getDTR();
        dtrInt += bitmask.getBitmask();
        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }

    @Command(names = {"flags remove", "flag remove"}, hidden = true, permission = "op")
    public static void bitmaskRemove(final Player sender, @Param(name = "team") final Team team, @Param(name = "bitmask") final DTRBitmask bitmask) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }
        if (!team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim doesn't have the bitmask value " + bitmask.getName() + ".");
            return;
        }
        int dtrInt = (int) team.getDTR();
        dtrInt -= bitmask.getBitmask();
        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }
}