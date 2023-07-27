package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.cmds.staff.FreezeCommand;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeamHQCommand {
    @Command(names = {"team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home", "faction home", "fac home", "home", "hq"}, permission = "")
    public static void teamHQ(final Player sender) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set, check your claim locations by typing /f claims");
            return;
        }
        if (Main.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters during the End of the World!");
            return;
        }
        if (FreezeCommand.getFreezes().containsKey(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you're frozen!");
            return;
        }
        if (SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(CC.RED + "You cannot teleport to your team headquarters while you're combat-tagged");
            return;
        }
        if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && !CustomTimerCreateCommand.hasSOTWEnabled(sender)) {
            Utils.removeThrownPearls(sender);
            sender.teleport(team.getHQ());
            return;
        }
        if (sender.getWorld().getEnvironment() == World.Environment.THE_END) {
            sender.sendMessage(CC.RED + "You cannot teleport to your team headquarters while you're at end world.");
            return;
        }

        HCFProfile profile = HCFProfile.get(sender);

        if (profile.hasPvPTimer()) {
            profile.setPvpTimer(null);
        } else {
            if (Main.getInstance().getServerHandler().getHomeTimer().containsKey(sender.getName())) {
                sender.sendMessage(CC.translate("&cYou are already being warped!"));
                return;
            }
            Main.getInstance().getServerHandler().beginHQWarp(sender, team, 10, false);
        }
    }
}