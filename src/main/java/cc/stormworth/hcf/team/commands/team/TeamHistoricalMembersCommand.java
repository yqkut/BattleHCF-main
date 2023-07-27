package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamHistoricalMembersCommand {

    @Command(names = {"team historicalmembers", "t historicalmembers", "f historicalmembers", "faction historicalmembers", "fac historicalmembers"}, permission = "MOD", async = true)
    public static void historicalmembers(final Player sender, @Param(name = "team") final Team team) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        sender.sendMessage(CC.YELLOW + team.getName() + "'s historical members:");
        final StringBuilder hmembers = new StringBuilder();
        for (final UUID uuid : team.getHistoricalMembers()) {
            hmembers.append(ChatColor.YELLOW).append(UUIDUtils.name(uuid)).append(ChatColor.GRAY).append(", ");
        }
        if (hmembers.length() > 2) {
            hmembers.setLength(hmembers.length() - 2);
        } else {
            hmembers.append(ChatColor.RED).append("Empty.");
        }
        sender.sendMessage(hmembers.toString());
    }
}