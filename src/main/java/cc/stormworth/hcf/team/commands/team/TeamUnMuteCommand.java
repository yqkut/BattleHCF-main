package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.punishment.PunishmentType;
import cc.stormworth.core.punishment.helpers.PunishmentHelper;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamUnMuteCommand {
    @Command(names = {"team unmute", "t unmute", "f unmute", "faction unmute", "fac unmute"}, permission = "ADMINISTRATOR")
    public static void teamUnMute(Player sender, @Param(name = "team") Team team) {

        TeamTrackerManager.logAsync(team, TeamActionType.TEAM_UMUTED, ImmutableMap.of(
                "unmutedById", sender.getUniqueId().toString(),
                "shadowMute", "false",
                "date", System.currentTimeMillis()
        ));

        for (UUID member : team.getMembers()) {
            new PunishmentHelper(sender, UUIDUtils.name(member), PunishmentType.MUTE, "team unmute", -1L, true, true);
        }
        sender.sendMessage(ChatColor.GRAY + "Unmuted the team " + team.getName() + ChatColor.GRAY + ".");
    }
}