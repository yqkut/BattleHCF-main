package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.punishment.PunishmentType;
import cc.stormworth.core.punishment.helpers.PunishmentHelper;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.time.DateUtil;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamMuteCommand {

    @Command(names = {"team mute", "t mute", "f mute", "faction mute", "fac mute"}, permission = "op")
    public static void teamMute(Player sender, @Param(name = "team") final Team team, @Param(name = "time") String time, @Param(name = "reason", wildcard = true) final String reason) {
        Long duration;
        try {
            duration = System.currentTimeMillis() - DateUtil.parseDateDiff(time, false);
        } catch (Exception var6) {
            sender.sendMessage(ChatColor.RED + "Failed to parse that duration.");
            return;
        }

        for (final UUID member : team.getMembers()) {
            new PunishmentHelper(sender, UUIDUtils.name(member), PunishmentType.MUTE, reason, duration, true, false);

            Player bukkitPlayer = Main.getInstance().getServer().getPlayer(member);

            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team has been muted for " + duration + " for " + reason + ".");
            }
        }

        TeamTrackerManager.logAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "false",
                "mutedById", sender.getUniqueId().toString(),
                "duration", time,
                "date", System.currentTimeMillis()
        ));

        sender.sendMessage(ChatColor.YELLOW + "Muted the team " + team.getName() + ChatColor.GRAY + " for " + duration + " for " + reason + ".");
    }
}