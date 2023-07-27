package cc.stormworth.hcf.misc.map.killstreaks;

import cc.stormworth.core.util.clazz.ClassUtils;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KillstreakHandler implements Listener {

  @Getter
  private final List<Killstreak> killstreaks = Lists.newArrayList();
  @Getter
  private final List<PersistentKillstreak> persistentKillstreaks = Lists.newArrayList();

  public KillstreakHandler() {
    Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    ClassUtils.getClassesInPackage(Main.getInstance(), "cc.stormworth.hcf.misc.map.killstreaks.prizes").forEach(clazz -> {
      if (Killstreak.class.isAssignableFrom(clazz)) {
        try {
          Killstreak killstreak = (Killstreak) clazz.newInstance();

          killstreaks.add(killstreak);
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }
      } else {
        try {
          PersistentKillstreak killstreak = (PersistentKillstreak) clazz.newInstance();

          persistentKillstreaks.add(killstreak);
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    });

    killstreaks.sort((first, second) -> {
      int firstNumber = first.getKills()[0];
      int secondNumber = second.getKills()[0];

      if (firstNumber < secondNumber) {
        return -1;
      }
      return 1;

    });

    persistentKillstreaks.sort((first, second) -> {
      int firstNumber = first.getKillsRequired();
      int secondNumber = second.getKillsRequired();

      if (firstNumber < secondNumber) {
        return -1;
      }
      return 1;

    });
  }

  @Command(names = "setks", permission = "PLATFORMADMINISTRATOR")
  public static void setKillstreak(CommandSender sender, @Param(name = "target") UUID target,
      @Param(name = "killstreak") int killstreak) {
    StatsEntry statsEntry = Main.getInstance().getMapHandler().getStatsHandler().getStats(target);
    statsEntry.setKillstreak(killstreak);

    sender.sendMessage(
        ChatColor.GREEN + "You set " + UUIDUtils.name(target) + " killstreak to: " + killstreak);
  }

  public Killstreak check(int kills) {
    for (Killstreak killstreak : killstreaks) {
      for (int kill : killstreak.getKills()) {
        if (kills == kill) {
          return killstreak;
        }
      }
    }

    return null;
  }

  public List<PersistentKillstreak> getPersistentKillstreaks(Player player, int count) {
    return persistentKillstreaks.stream().filter(s -> s.check(count)).collect(Collectors.toList());
  }

    /*private void grantTeamKillstreakReward(Player player, Team team, int killstreak, int points) {
        team.addKillstreakPoints(points);
        team.sendMessage(ChatColor.GREEN + "Your team received " + points + " points thanks to " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GREEN + "'s " + killstreak + " killstreak.");
    }

    @EventHandler
    public void onPlayerKilledEvent(PlayerKilledEvent event) {
        StatsEntry killerStats = Main.getInstance().getMapHandler().getStatsHandler().getStats(event.getKiller());
        Team killerTeam = Main.getInstance().getTeamHandler().getTeam(event.getKiller());

        if (killerTeam != null) {
            switch (killerStats.getKillstreak()) {
                case 75:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 75, 15);
                    break;
                case 150:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 150, 25);
                    break;
                case 300:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 300, 30);
                    break;
                case 400:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 400, 40);
                    break;
                case 500:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 500, 50);
                    break;
                case 1000:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 1000, 100);
                    break;
            }
        }
    }*/
}