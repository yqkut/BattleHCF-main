package cc.stormworth.hcf.misc.map.stats.command;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class StatModifyCommands {
    @Command(names = {"modifystat setkills"}, permission = "op", hidden = true)
    public static void setKills(final CommandSender player, @Param(name = "target") UUID target, @Param(name = "kills") final int kills) {
        final StatsEntry stats = Main.getInstance().getMapHandler().getStatsHandler().getStats(target);
        HCFProfile profile = HCFProfile.getByUUID(target);
        stats.setKills(kills);
        profile.setKills(kills);
        player.sendMessage(ChatColor.GREEN + "You've set " + UUIDUtils.name(target) + "'s kills to: " + kills);
    }

    @Command(names = {"modifystat setdeaths"}, permission = "op", hidden = true)
    public static void setDeaths(final CommandSender player, @Param(name = "target") UUID target, @Param(name = "deaths") final int deaths) {
        final StatsEntry stats = Main.getInstance().getMapHandler().getStatsHandler().getStats(target);
        HCFProfile profile = HCFProfile.getByUUID(target);
        stats.setDeaths(deaths);
        profile.setDeaths(deaths);
        player.sendMessage(ChatColor.GREEN + "You've set " + UUIDUtils.name(target) + "'s deaths to: " + deaths);
    }

    @Command(names = {"modifystat setteamkills"}, permission = "op", hidden = true)
    public static void setTeamKills(final CommandSender player, @Param(name = "target") UUID target, @Param(name = "kills") final int kills) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(target);
        if (team != null) {
            team.setKills(kills);
            player.sendMessage(ChatColor.GREEN + "You've set " + UUIDUtils.name(target) + "'s  team kills to: " + kills);
        }
    }

    @Command(names = {"modifystat setteamdeaths"}, permission = "op", hidden = true)
    public static void setTeamDeaths(final CommandSender player, @Param(name = "target") UUID target, @Param(name = "deaths") final int deaths) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(target);
        if (team != null) {
            team.setDeaths(deaths);
            player.sendMessage(ChatColor.GREEN + "You've set " + UUIDUtils.name(target) + "'s  team deaths to: " + deaths);
        }
    }
}