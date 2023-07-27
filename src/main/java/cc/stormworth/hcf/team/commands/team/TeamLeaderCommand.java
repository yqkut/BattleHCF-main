package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamLeaderCommand {
    @Command(names = {"team newleader", "t newleader", "f newleader", "faction newleader", "fac newleader", "team leader", "t leader", "f leader", "faction leader", "fac leader"}, permission = "")
    public static void teamLeader(final Player sender, @Param(name = "player") final UUID uuid) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
            return;
        }
        if (!team.isMember(uuid)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(uuid) + " is not on your team.");
            return;
        }
        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(uuid) + " has been given ownership of " + team.getName() + ".");
        team.setOwner(uuid);
        team.addCaptain(sender.getUniqueId());
    }
}