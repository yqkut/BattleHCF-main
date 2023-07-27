package cc.stormworth.hcf.misc.map.stats.command;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.leaderboards.LeaderboardMenu;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatsTopCommand {

    @Command(names = {"leaderboards", "leaderboard"}, permission = "", async = true)
    public static void leaderboards(final CommandSender sender,
        @Param(name = "objective", defaultValue = "kills") final StatsObjective objective) {
        sender.sendMessage(
            ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(
            ChatColor.YELLOW + "Leaderboards for: " + ChatColor.RED + objective.getName());
        sender.sendMessage(
            ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        int index = 0;
        for (Map.Entry<StatsEntry, String> entry : Main.getInstance().getMapHandler().getStatsHandler().getLeaderboards(objective, 10).entrySet()) {
            ++index;
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(index).append(". ")
                .append(ChatColor.YELLOW).append(ChatColor.YELLOW)
                .append(UUIDUtils.name(entry.getKey().getOwner())).append(ChatColor.YELLOW)
                .append(" - ").append(ChatColor.RED).append(entry.getValue()).toString());
        }
        sender.sendMessage(
            ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

    @Command(names = {"leaderboards", "leaderboard"}, permission = "", async = true)
    public static void leaderboards(final Player sender) {
        new LeaderboardMenu().openMenu(sender);
    }

    public enum StatsObjective {
        KILLS("Kills", new String[]{"k"}),
        DEATHS("Deaths", new String[]{"d"}),
        KD("KD", new String[]{"kdr"}),
        HIGHEST_KILLSTREAK("Highest Killstreak",
            new String[]{"killstreak", "highestkillstreak", "ks", "highestks", "hks"});

        private final String name;
        private final String[] aliases;

        StatsObjective(final String name, final String[] aliases) {
            this.name = name;
            this.aliases = aliases;
        }

        public String getName() {
            return this.name;
        }

        public String[] getAliases() {
            return this.aliases;
        }
    }
}