package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetTeamBalanceCommand {

    @Command(names = {"setteambalance", "setteambal"}, permission = "PLATFORMADMINISTRATOR")
    public static void setTeamBalance(final Player sender, @Param(name = "team") final Team team, @Param(name = "balance") final int balance) {
        team.setBalance(balance);
        sender.sendMessage(ChatColor.GOLD + team.getName() + ChatColor.YELLOW + "'s balance is now " + ChatColor.GOLD + team.getBalance() + ChatColor.YELLOW + ".");
    }
}