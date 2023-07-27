package cc.stormworth.hcf.listener;

import cc.stormworth.core.cmds.staff.FreezeCommand;
import cc.stormworth.core.server.utils.FreezeInfo;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FFASOTWListener implements Runnable {

  public static BukkitTask ffaTask;

  public FFASOTWListener() {
    CustomTimerCreateCommand.ffasotw = true;
    List<Team> teams = new ArrayList<>();

    for (Team team : Main.getInstance().getTeamHandler().getTeams()) {
      teams.add(team);
    }

    for (Team team : teams) {
      team.disband();
    }

    Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been disbanded!");

    for (UUID spectator : SpectatorListener.spectators) {
      Player player = Bukkit.getPlayer(spectator);
      if (player != null) {
        TaskUtil.run(Main.getInstance(), () -> SpectatorListener.disableSpectator(player));
      }
    }
    SpectatorListener.spectators.clear();

    CustomTimerCreateCommand.getCustomTimers().put("&a&lSOTW Timer", System.currentTimeMillis() + TimeUtils.parseTime("10m") * 1000);

    ffaTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this, 0, 20L);

    Main.getInstance().getEventHandler().getEvents().clear();
    Main.getInstance().getEventHandler().saveEvents();
    Bukkit.setWhitelist(true);
  }

  public void disable() {
    if (ffaTask != null) {
      ffaTask.cancel();
      ffaTask = null;
    }
  }

  @Override
  public void run() {
    long diff = (CustomTimerCreateCommand.getSOTWremaining() - System.currentTimeMillis()) / 1000;

    if (diff == 120) {
      EOTWCommand.startFFA();
    }

    if (diff == 100) {
      SetListener.loadEotwFFA();
      for (Player online : Bukkit.getOnlinePlayers()) {
        if (online.hasPermission("core.staff")) {
          continue;
        }
        FreezeCommand.getFreezes().put(online.getUniqueId(), new FreezeInfo(null, online.getUniqueId(), SetListener.getEotwffa(), System.currentTimeMillis()));
      }
    }

    if (diff == 60) {
      int timesrun = 0;
      for (Player online : Bukkit.getOnlinePlayers()) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {

          if (!online.hasPermission("core.staff")) {
            online.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
          }

          online.teleport(SetListener.getEotwffa());
          FreezeCommand.getFreezes().remove(online.getUniqueId());
        }, (timesrun * 1L));
        timesrun++;
      }
    }
    if (diff == 0) {
      CustomTimerCreateCommand.getCustomTimers().remove("&a&lSOTW Timer");
      this.disable();
    }
  }
}