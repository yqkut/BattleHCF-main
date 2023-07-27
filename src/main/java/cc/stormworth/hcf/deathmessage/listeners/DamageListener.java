package cc.stormworth.hcf.deathmessage.listeners;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.gson.serialization.PlayerInventorySerializer;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.deathmessage.DeathMessageHandler;
import cc.stormworth.hcf.deathmessage.event.CustomPlayerDamageEvent;
import cc.stormworth.hcf.deathmessage.event.PlayerKilledEvent;
import cc.stormworth.hcf.deathmessage.objects.Damage;
import cc.stormworth.hcf.deathmessage.objects.PlayerDamage;
import cc.stormworth.hcf.deathmessage.util.UnknownDamage;
import cc.stormworth.hcf.listener.WebsiteListener;
import cc.stormworth.hcf.misc.map.killstreaks.Killstreak;
import cc.stormworth.hcf.misc.map.killstreaks.PersistentKillstreak;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.KillBoosting;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.player.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DamageListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityDamage(final EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();
      CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player, event.getDamage()));

      Main.getInstance().getServer().getPluginManager().callEvent(customEvent);
      DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
    }
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    DeathMessageHandler.clearDamage(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(final PlayerDeathEvent event) {

    if(Main.getInstance().getTeamHandler().getTeam(event.getEntity()) != null &&
            Main.getInstance().getFactionDuelManager().isInMatch(Main.getInstance().getTeamHandler().getTeam(event.getEntity()))){
      return;
    }

    SpawnTagHandler.removeTag(event.getEntity());

    List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());

    event.setDeathMessage(null);

    Player victim = event.getEntity();

    HCFProfile victimProfile = HCFProfile.getByUUID(victim.getUniqueId());

    if(!Main.getInstance().getMapHandler().isKitMap() && victimProfile.isDeathBanned()){
      return;
    }

    /*if (!Main.getInstance().getMapHandler().isKitMap() && Main.getInstance().getDeathbannedMap().isDeathbanned(event.getEntity().getUniqueId())) {
      if (event.getEntity().getKiller() != null &&
              Main.getInstance().getDeathbannedMap().isDeathbanned(event.getEntity().getKiller().getUniqueId()) &&
              !Main.getInstance().getDeathbannedMap().isDeathbanned(event.getEntity().getUniqueId())) {
        return;
      }

      if (event.getEntity().getKiller() != null &&
              Main.getInstance().getDeathbannedMap().isDeathbanned(event.getEntity().getKiller().getUniqueId())) {
        Main.getInstance().getDeathbanMap().removeTime(event.getEntity().getKiller().getUniqueId(), 300);
      }
      return;
    }

    if (!Main.getInstance().getMapHandler().isKitMap()) {
      Main.getInstance().getDeathbannedMap().setDeathbanned(event.getEntity().getUniqueId(), true);
    }*/

    Clickable deathMessage;
    boolean countkill = false;
    if (record != null) {
      final Damage deathCause = record.get(record.size() - 1);
      if (deathCause instanceof PlayerDamage
          && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1L)) {

        final Player killer = ((PlayerDamage) deathCause).getDamager();
        if (killer != null) {

          ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();

          if (Main.getInstance().getMapHandler().isKitMap()) {
            final PlayerKilledEvent killedEvent = new PlayerKilledEvent(killer, victim);
            Main.getInstance().getServer().getPluginManager().callEvent(killedEvent);

            /*if (this.lastKilled.containsKey(killer.getUniqueId())
                && this.lastKilled.get(killer.getUniqueId()) == victim.getUniqueId()) {
              this.boosting.putIfAbsent(killer.getUniqueId(), 0);
              this.boosting.put(killer.getUniqueId(), this.boosting.get(killer.getUniqueId()) + 1);
            } else {
              this.boosting.put(killer.getUniqueId(), 0);
            }*/

            HCFProfile killerProfile = HCFProfile.getByUUID(killer.getUniqueId());

            if(killerProfile == null){
              return;
            }

            KillBoosting boost = killerProfile.getByTarget(victim.getUniqueId());

            if (boost == null) {
              boost = new KillBoosting(victim.getUniqueId());
            }

            boost.setCooldown(System.currentTimeMillis() + TimeUtil.parseTimeLong("7m"));

            boost.addKills(1);

            killerProfile.getBoostings().add(boost);

            if (killer.equals(victim) || Players.isNaked(victim)) {
              Main.getInstance().getMapHandler().getStatsHandler().getStats(victim).addDeath();
            } else if (!killer.isOp() && killer.getAddress().getAddress().getHostAddress().equalsIgnoreCase(victim.getAddress().getAddress().getHostAddress())) {
              killer.sendMessage(ChatColor.RED
                  + "Boost Check: You've killed a player on the same IP address as you.");
            } else if (!killer.isOp() && boost.getKills() >= 5 && boost.isOnCooldown()) {

              killer.sendMessage(ChatColor.RED + "Boost Check: You've killed " + victim.getName() + " " + boost.getKills() + " times.");

              StatsEntry victimStats2 = Main.getInstance().getMapHandler().getStatsHandler().getStats(victim);

              victimStats2.addDeath();
              victimProfile.setDeaths(victimStats2.getDeaths());
            } else {
              StatsEntry victimStats = Main.getInstance().getMapHandler().getStatsHandler().getStats(victim);
              StatsEntry killerStats = Main.getInstance().getMapHandler().getStatsHandler().getStats(killer);

              victimStats.addDeath();
              killerStats.addKill();

              Killstreak killstreak = Main.getInstance().getMapHandler().getKillstreakHandler().check(killerStats.getKillstreak());

              if (killstreak != null) {
                killstreak.apply(killer);

                Bukkit.broadcastMessage(
                    ChatColor.GOLD + "[KillStreaks] " + killer.getDisplayName() + ChatColor.YELLOW
                        + " has gotten the " + ChatColor.RED + killstreak.getName()
                        + ChatColor.YELLOW + " killstreak!");

                List<PersistentKillstreak> persistent = Main.getInstance().getMapHandler()
                    .getKillstreakHandler()
                    .getPersistentKillstreaks(killer, killerStats.getKillstreak());

                for (final PersistentKillstreak persistentStreak : persistent) {
                  if (persistentStreak.matchesExactly(killerStats.getKillstreak())) {
                    Bukkit.broadcastMessage(
                        ChatColor.GOLD + "[KillStreaks] " + killer.getDisplayName()
                            + ChatColor.YELLOW + " has gotten the " + ChatColor.RED
                            + killstreak.getName() + ChatColor.YELLOW + " killstreak!");
                  }
                  persistentStreak.apply(killer);
                }
              }

              HCFProfile profile = HCFProfile.get(killer);


              killer.sendMessage(CC.translate("&aYou have received a Blood key."));

              if (Main.getInstance().getTeamHandler().getTeam(killer) != null) {
                Team team = Main.getInstance().getTeamHandler().getTeam(killer);
                team.addPoints(1);
              }

              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + killer.getName() + " Evil 1");

              if (profile != null) {

                profile.getEconomyData().addBalance(100 + this.getAdditional(killer));

                profile.addKills(1);

                if (!Main.getInstance().getEventHandler().getEclipseEvent().isActive()){
                  if (Main.getInstance().getMapHandler().isKitMap()) {
                    profile.addGems(2);
                    killer.sendMessage(CC.translate("&aYou have received 2x gems."));
                  } else {
                    profile.addGems(5);
                    killer.sendMessage(CC.translate("&aYou have received 5x gems."));
                  }
                }else{
                  profile.addGems(20);
                  killer.sendMessage(CC.translate("&aYou have received 5x gems."));
                }


                profile.getEconomyData().addBalance(300);

                killer.sendMessage(CC.translate("&6You have received a 300 of balance."));
              }

              killerProfile.setKills(killerStats.getKills());
              victimProfile.setDeaths(victimStats.getDeaths());
              countkill = true;

              if (!boost.isOnCooldown()) {
                boost.setCooldown(System.currentTimeMillis() + TimeUtil.parseTimeLong("7m"));
              }

            }
          } else {
            /*if (this.lastKilled.containsKey(killer.getUniqueId())
                && this.lastKilled.get(killer.getUniqueId()) == victim.getUniqueId()) {
              this.boosting.putIfAbsent(killer.getUniqueId(), 0);
              this.boosting.put(killer.getUniqueId(), this.boosting.get(killer.getUniqueId()) + 1);
            } else {
              this.boosting.put(killer.getUniqueId(), 0);
            }*/

            HCFProfile killerProfile = HCFProfile.getByUUID(killer.getUniqueId());

            KillBoosting boost = killerProfile.getByTarget(victim.getUniqueId());

            if (boost == null) {
              boost = new KillBoosting(victim.getUniqueId());
            }

            boost.setCooldown(System.currentTimeMillis() + TimeUtil.parseTimeLong("7m"));
            boost.addKills(1);

            killerProfile.getBoostings().add(boost);

            if (killer.equals(victim) || Players.isNaked(victim)) {
              final StatsEntry victimStats2 = Main.getInstance().getMapHandler().getStatsHandler()
                  .getStats(victim);
              victimStats2.addDeath();
              victimProfile.setDeaths(victimStats2.getDeaths());
            } else if (!killer.isOp() && killer.getAddress().getAddress().getHostAddress()
                .equalsIgnoreCase(victim.getAddress().getAddress().getHostAddress())) {
              killer.sendMessage(ChatColor.RED
                  + "Boost Check: You've killed a player on the same IP address as you.");
            } else if (!killer.isOp() && boost.getKills() >= 5 && boost.isOnCooldown()) {
              killer.sendMessage(
                  ChatColor.RED + "Boost Check: You've killed " + victim.getName() + " "
                      + boost.getKills() + " times.");
              final StatsEntry victimStats2 = Main.getInstance().getMapHandler().getStatsHandler()
                  .getStats(victim);
              victimStats2.addDeath();
              victimProfile.setDeaths(victimStats2.getDeaths());
            } else {
              final StatsEntry killerStats2 = Main.getInstance().getMapHandler().getStatsHandler()
                  .getStats(killer);
              killerStats2.addKill();

              HCFProfile profile = HCFProfile.getByUUID(killer.getUniqueId());
              if (profile != null) {
                profile.addKills(1);
                profile.addGems(2);
                killer.sendMessage(CC.translate("&aYou have received 2x gems."));
              }

              event.getDrops().add(Main.getInstance().getServerHandler()
                  .generateDeathSign(event.getEntity().getName(), killer.getName()));
              countkill = true;

              if (!boost.isOnCooldown()) {
                boost.setCooldown(System.currentTimeMillis() + TimeUtil.parseTimeLong("7m"));
              }
            }
          }
        }
      }

      deathMessage = deathCause.getDeathMessage();
    } else {
      deathMessage = new UnknownDamage(event.getEntity(), 1.0).getDeathMessage();
    }

    final Player killer = event.getEntity().getKiller();
    final Team killerTeam =
        (killer == null) ? null : Main.getInstance().getTeamHandler().getTeam(killer);
    final Team deadTeam = Main.getInstance().getTeamHandler().getTeam(event.getEntity());
    if (killerTeam != null && countkill && !killerTeam.getHistoricalMembers()
        .contains(event.getEntity().getUniqueId())) {
      killerTeam.setKills(killerTeam.getKills() + 1);
    }
    if (deadTeam != null) {
      deadTeam.setDeaths(deadTeam.getDeaths() + 1);
    }
    if (!CustomTimerCreateCommand.isSOTWTimer()) {
      Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
          HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());
          if (profile.isDeathMessages()) {
            deathMessage.sendToPlayer(player);
          } else {
            if (Main.getInstance().getTeamHandler().getTeam(player) == null) {
              continue;
            }

            // send them the message if the player who died was on their team
            if (Main.getInstance().getTeamHandler().getTeam(event.getEntity()) != null
                && Main.getInstance().getTeamHandler().getTeam(player)
                .equals(Main.getInstance().getTeamHandler().getTeam(event.getEntity()))) {
              deathMessage.sendToPlayer(player);
            }

            // send them the message if the killer was on their team
            if (killer != null) {
              if (Main.getInstance().getTeamHandler().getTeam(killer) != null && Main.getInstance()
                  .getTeamHandler().getTeam(player)
                  .equals(Main.getInstance().getTeamHandler().getTeam(killer))) {
                deathMessage.sendToPlayer(player);
              }
            }
          }
        }
      });
    }

    WebsiteListener.saveDeath(event.getEntity().getUniqueId(),
        killer != null ? killer.getUniqueId() : null,
        PlayerInventorySerializer.getInsertableObject(event.getEntity()),
        deathMessage.toString(), //Maybe need change this xd
        (killer != null && killer.getItemInHand() != null ? killer.getItemInHand() : null),
        event.getEntity().getLocation());

    DeathMessageHandler.clearDamage(event.getEntity());

    StatsEntry victimStats3 = Main.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity());
    if (!Main.getInstance().getMapHandler().isKitMap()) {
      victimStats3.addDeath();
    }
    HCFProfile.getByUUID(event.getEntity().getUniqueId()).setDeaths(victimStats3.getDeaths());
  }

  private int getAdditional(final Player killer) {
    Profile profile = Profile.getByUuidIfAvailable(killer.getUniqueId());
    if (profile.getRank() == Rank.WARRIOR) {
      return 10;
    }
    if (profile.getRank() == Rank.KNIGHT) {
      return 20;
    }
    if (profile.getRank() == Rank.SOLDIER) {
      return 30;
    }
    if (profile.getRank() == Rank.HERO) {
      return 40;
    }
    if (profile.getRank() == Rank.MASTER) {
      return 50;
    }
    if (profile.getRank() == Rank.KING) {
      return 60;
    }
    if (profile.getRank() == Rank.KING_PLUS) {
      return 75;
    }
    if (profile.getRank() == Rank.BATTLE) {
      return 100;
    }
    if (profile.getRank() == Rank.MINI_YT) {
      return 150;
    }
    if (profile.getRank().isAboveOrEqual(Rank.STREAMER)) {
      return 175;
    }
    return 0;
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    if ((Main.getInstance().getServerHandler().isEOTW() || Main.getInstance().getServerHandler().isPreEOTW())) {

      if (!event.getPlayer().hasPermission("core.staff")){
        TaskUtil.runLater(Main.getInstance(), () -> event.getPlayer().kickPlayer("You died in the EOTW"), 20L);
      }
    }

    if (Main.getInstance().getMapHandler().isKitMap()) {
      this.checkKillstreaks(event.getPlayer());
    }

    /*if (event.getPlayer().hasPermission("core.staff") || !Main.getInstance().getDeathbannedMap()
        .isDeathbanned(event.getPlayer().getUniqueId()) || !Main.getInstance().getServerHandler()
        .isEOTW() || !Main.getInstance().getServerHandler().isPreEOTW()) {
      return;
    }

    TaskUtil.runLater(Main.getInstance(), () -> SpectatorListener.enableSpectator(event.getPlayer()), 20L);*/
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      this.checkKillstreaks(event.getPlayer());
    }
  }

  private void checkKillstreaks(Player player) {
    if (EOTWCommand.isFfaEnabled() || Main.getInstance().getServerHandler().isEOTW()
        || Main.getInstance().getServerHandler().isPreEOTW()) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
      int killstreak = Main.getInstance().getMapHandler().getStatsHandler().getStats(player).getKillstreak();

      List<PersistentKillstreak> persistent = Main.getInstance().getMapHandler()
          .getKillstreakHandler().getPersistentKillstreaks(player, killstreak);

      for (PersistentKillstreak persistentStreak : persistent) {
        persistentStreak.apply(player);
      }
    }, 5L);
  }

  @EventHandler
  public void onRightClick(final PlayerInteractEvent event) {
    if (!event.getAction().name().startsWith("RIGHT_CLICK")) {
      return;
    }
    final ItemStack inHand = event.getPlayer().getItemInHand();
    if (inHand == null) {
      return;
    }
    if (inHand.getType() != Material.NETHER_STAR) {
      return;
    }
    if (!inHand.hasItemMeta() || !inHand.getItemMeta().hasDisplayName() || !inHand.getItemMeta()
        .getDisplayName()
        .startsWith(ChatColor.RED.toString() + ChatColor.BOLD + "Potion Refill Token")) {
      return;
    }
    if (EOTWCommand.isFfaEnabled()) {
      return;
    }
    event.getPlayer().setItemInHand(null);
    final ItemStack pot = new ItemStack(Material.POTION, 1, (short) 16421);
    while (event.getPlayer().getInventory().addItem(new ItemStack[]{pot}).isEmpty()) {
    }
  }
}