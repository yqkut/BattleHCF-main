package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamOpenCommand {

    @Command(names = {"team open", "t open", "f open", "faction open", "fac open"}, permission = "")
    public static void open(final Player sender) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team leaders can do this.");
            return;
        }
        team.setOpen(!team.isOpen());
        String watcherName = ChatColor.DARK_GREEN + sender.getName();
        if (team.isOwner(sender.getUniqueId())) {
            watcherName = watcherName + ChatColor.GRAY + "**";
        } else if (team.isCoLeader(sender.getUniqueId())) {
            watcherName = watcherName + ChatColor.GRAY + "**";
        } else if (team.isCaptain(sender.getUniqueId())) {
            watcherName = watcherName + ChatColor.GRAY + "*";
        }
        team.sendMessage(CC.translate(watcherName + " &ehas " + (team.isOpen() ? "&aOpened" : "&cClosed") + " &eyour faction!"));
    }
}