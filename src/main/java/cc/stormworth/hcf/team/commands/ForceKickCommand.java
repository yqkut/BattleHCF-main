package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceKickCommand {

  @Command(names = {"forcekick"}, permission = "SENIORMOD")
  public static void forceKick(Player sender, @Param(name = "player") UUID player) {
    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    if (team == null) {
      sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on a team!");
      return;
    }

    if (team.getMembers().size() == 1) {
      sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + "'s team has one member. Please use /forcedisband to perform this action.");
      return;
    }

    team.removeMember(player);
    Main.getInstance().getTeamHandler().setTeam(player, null);

    Player bukkitPlayer = Bukkit.getPlayer(player);

    if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
      CorePlugin.getInstance().getNametagEngine().reloadPlayer(bukkitPlayer);
      CorePlugin.getInstance().getNametagEngine().reloadOthersFor(bukkitPlayer);
      bukkitPlayer.sendMessage(ChatColor.RED + "You were kicked from your team by a staff member.");
      team.getActiveEffects().keySet().forEach(bukkitPlayer::removePotionEffect);
    }

    sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.GOLD + UUIDUtils.name(player)
        + ChatColor.YELLOW + " from their team, " + ChatColor.GOLD + team.getName()
        + ChatColor.YELLOW + ".");
  }

  @Command(names = {"resetteam"}, permission = "SENIORMOD")
  public static void resetteam(Player sender, @Param(name = "player") UUID player) {

    Main.getInstance().getTeamHandler().setTeam(player, null);
    Player bukkitPlayer = Bukkit.getPlayer(player);

    if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
      CorePlugin.getInstance().getNametagEngine().reloadPlayer(bukkitPlayer);
      CorePlugin.getInstance().getNametagEngine().reloadOthersFor(bukkitPlayer);
      bukkitPlayer.sendMessage(ChatColor.RED + "You were kicked from your team by a staff member.");
    }

    sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.GOLD + UUIDUtils.name(player) + ChatColor.YELLOW + " from their team" + ChatColor.YELLOW + ".");
  }
}