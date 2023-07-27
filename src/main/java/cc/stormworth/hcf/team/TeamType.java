package cc.stormworth.hcf.team;

import cc.stormworth.core.util.command.param.ParameterType;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamType implements ParameterType<Team> {
    public Team transform(final CommandSender sender, final String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = Main.getInstance().getTeamHandler().getTeam((Player) sender);
            if (team == null) {
                sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
                return null;
            }
            return team;
        } else {
            Player bukkitPlayer = Main.getInstance().getServer().getPlayer(source);
            if (bukkitPlayer != null) {
                final Team byMemberBukkitPlayer = Main.getInstance().getTeamHandler().getTeam(bukkitPlayer.getUniqueId());
                if (byMemberBukkitPlayer != null) {
                    return byMemberBukkitPlayer;
                }
            }

            Team byMemberUUID = Main.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(source));
            if (byMemberUUID != null) {
                return byMemberUUID;
            }

            final Team byName = Main.getInstance().getTeamHandler().getTeam(source);
            if (byName != null) {
                return byName;
            }

            sender.sendMessage(ChatColor.RED + "No team or member with the name " + source + " found.");
            return null;
        }
    }

}