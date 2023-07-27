package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.fancy.FormatingMessage;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class TeamTopCommand {

    @Command(names = {"team top", "t top", "f top", "faction top", "fac top", "ftop", "ttop"}, permission = "", async = true)
    public static void top(final CommandSender sender) {
        LinkedHashMap<Team, Integer> sortedTeamPlayerCount = getTopTeams();

        int index = 0;

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(CC.B_YELLOW + "Team Top Points");
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

            if (teamEntry.getKey().getOwner() == null) {
                continue;
            }

            index++;

            if (11 <= index) {
                break;
            }

            FormatingMessage teamMessage = new FormatingMessage();

            Team team = teamEntry.getKey();
            teamMessage.text(index + ". " + (teamEntry.getKey().isDisqualified() ? CC.BD_RED + "✘ " : "")).color(ChatColor.GRAY).then();
            teamMessage.text(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                    .tooltip((sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + teamEntry.getKey().getName() + "\n" +
                            ChatColor.GOLD + "Leader: " + ChatColor.GRAY + UUIDUtils.name(teamEntry.getKey().getOwner()) + "\n\n" +
                            ChatColor.GOLD + "Balance: " + ChatColor.GRAY + "$" + team.getBalance() + "\n" +
                            ChatColor.GOLD + "Kills: " + ChatColor.GRAY + team.getKills() + "\n" +
                            ChatColor.GOLD + "Deaths: " + ChatColor.GRAY + team.getDeaths() + "\n\n" +
                            //ChatColor.GOLD + "Raids: " + ChatColor.GRAY + team.getRaids() + "\n\n" +
                            ChatColor.GOLD + "KOTH Captures: " + ChatColor.GRAY + team.getKothCaptures() + "\n" +
                            ChatColor.GOLD + "Diamonds Mined: " + ChatColor.GRAY + team.getDiamondsMined() + "\n\n" +
                            ChatColor.GOLD + "Qualifies: " + ChatColor.GRAY + (team.isDisqualified() ? "No" : "Yes") + "\n" +
                            ChatColor.GREEN + "Click to view team info").command("/t who " + teamEntry.getKey().getName()).then();
            teamMessage.text(" - ").color(ChatColor.YELLOW).then();
            teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.GRAY);

            teamMessage.send(sender);
        }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

    /*@Command(names = {"team top raidable", "t top raidable", "f top raidable", "faction top raidable", "fac top raidable", "team top raid", "t top raid", "f top raid", "faction top raid", "fac top raid", "team top raids", "t top raids", "f top raids", "faction top raids", "fac top raids"}, permission = "", async = true)
    public static void raidable(final CommandSender sender) {
        LinkedHashMap<Team, Integer> sortedTeamPlayerCount = getRaidsTeams();

        int index = 0;

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(CC.B_YELLOW + "Team Top Raids");
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

            if (teamEntry.getKey().getOwner() == null) {
                continue;
            }

            index++;

            if (11 <= index) {
                break;
            }

            FormatingMessage teamMessage = new FormatingMessage();

            Team team = teamEntry.getKey();

            teamMessage.text(index + ". " + (teamEntry.getKey().isDisqualified() ? CC.BD_RED + "✘ " : "")).color(ChatColor.GRAY).then();
            teamMessage.text(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                    .tooltip((sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + teamEntry.getKey().getName() + "\n" +
                            ChatColor.GOLD + "Leader: " + ChatColor.GRAY + UUIDUtils.name(teamEntry.getKey().getOwner()) + "\n\n" +
                            ChatColor.GOLD + "Balance: " + ChatColor.GRAY + "$" + team.getBalance() + "\n" +
                            ChatColor.GOLD + "Kills: " + ChatColor.GRAY + team.getKills() + "\n" +
                            ChatColor.GOLD + "Deaths: " + ChatColor.GRAY + team.getDeaths() + "\n\n" +
                            ChatColor.GOLD + "Raids: " + ChatColor.GRAY + team.getRaids() + "\n\n" +
                            ChatColor.GOLD + "KOTH Captures: " + ChatColor.GRAY + team.getKothCaptures() + "\n" +
                            ChatColor.GOLD + "Diamonds Mined: " + ChatColor.GRAY + team.getDiamondsMined() + "\n\n" +
                            ChatColor.GOLD + "Qualifies: " + ChatColor.GRAY + (team.isDisqualified() ? "No" : "Yes") + "\n" +
                            ChatColor.GREEN + "Click to view team info").command("/t who " + teamEntry.getKey().getName()).then();
            teamMessage.text(" - ").color(ChatColor.YELLOW).then();
            teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.GRAY);

            teamMessage.send(sender);
        }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }*/

    public static LinkedHashMap<Team, Integer> getRaidsTeams() {
        Map<Team, Integer> teamRaidsCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Team team : Main.getInstance().getTeamHandler().getTeams()) {
            teamRaidsCount.put(team, team.getRaids());
        }

        return sortByValues(teamRaidsCount);
    }

    public static LinkedHashMap<Team, Integer> getTopTeams() {
        Map<Team, Integer> teamPointsCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Team team : Main.getInstance().getTeamHandler().getTeams()) {
            teamPointsCount.put(team, team.getPoints());
        }

        return sortByValues(teamPointsCount);
    }

    public static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
        LinkedList<Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

        list.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }
}