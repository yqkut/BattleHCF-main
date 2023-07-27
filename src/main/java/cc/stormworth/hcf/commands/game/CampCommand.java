package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.commands.team.TeamStuckCommand;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.cooldowntimer.TimerManager;
import cc.stormworth.hcf.util.player.Camp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CampCommand {

  private static final double MAX_DISTANCE = 5;
  public static Map<String, Long> warping = new HashMap<>();
  public static List<String> damaged = Lists.newArrayList();
  @Getter
  public static Map<String, Camp> campTasks;

  @Command(names = {"camp"}, permission = "")
  public static void camp(final Player sender, @Param(name = "team") Team target) {
    if (!Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis is a KitMap only command."));
      return;
    }
    if (SpawnTagHandler.isTagged(sender)) {
      sender.sendMessage(CC.RED + "You cannot camp while you're combat-tagged");
      return;
    }

    Team team = LandBoard.getInstance().getTeam(sender.getLocation());

    if (team == null || (!team.isMember(sender.getUniqueId()) && !DTRBitmask.SAFE_ZONE.appliesAt(
        sender.getLocation()))) {
      sender.sendMessage(
          CC.translate("&cThis command is only available in safe-zone or own claims."));
      return;
    }
    if (target.isMember(sender.getUniqueId())) {
      sender.sendMessage(CC.RED + "You cannot camp your team.");
      return;
    }
    if (target.getOwner() == null) {
      sender.sendMessage(CC.RED + "The team you selected don't have members.");
      return;
    }
    if (target.getHQ() == null) {
      sender.sendMessage(
          CC.RED + "Was not possible to find a near location for " + target.getName() + ".");
      return;
    }

    if (DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
      Location nearest = TeamStuckCommand.nearestSafeLocation(target.getHQ());
      sender.teleport(nearest);
    } else {
      if (warping.containsKey(sender.getName())) {
        sender.sendMessage(ChatColor.RED + "You are already being warped!");
        return;
      }

      if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
        sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
        return;
      }

      int seconds = 20;
      warping.put(sender.getName(),
          System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds));

      CooldownManager.addCooldown(sender.getUniqueId(), CooldownType.CAMP, seconds);
      TimerManager.getInstance().getCooldownTimer().activate(sender, "camp", 20, null);

      new BukkitRunnable() {

        private final Location loc = sender.getLocation();
        private final int xStart = (int) loc.getX();
        private final int yStart = (int) loc.getY();
        private final int zStart = (int) loc.getZ();
        private int seconds = 20;
        private Location nearest = TeamStuckCommand.nearestSafeLocation(target.getHQ());

        @Override
        public void run() {
          if (damaged.contains(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You took damage, teleportation cancelled!");
            CooldownManager.removeCooldown(sender.getUniqueId(), CooldownType.CAMP);
            TimerManager.getInstance().getCooldownTimer().cancel(sender.getUniqueId(), "camp");
            damaged.remove(sender.getName());
            warping.remove(sender.getName());
            cancel();
            return;
          }

          if (!sender.isOnline()) {
            warping.remove(sender.getName());
            cancel();
            return;
          }

          if (seconds == 5) {
            new BukkitRunnable() {
              @Override
              public void run() {
                nearest = TeamStuckCommand.nearestSafeLocation(target.getHQ());
              }
            }.runTask(Main.getInstance());
          }

          Location loc = sender.getLocation();

          if (seconds <= 0) {
            sender.teleport(nearest);
            sender.sendMessage(
                ChatColor.YELLOW + "Teleported you to the nearest area to " + target.getName(sender)
                    + ".");

            warping.remove(sender.getName());
            cancel();
            return;
          }
          if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (
              loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (
              loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
            sender.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE
                + " blocks, teleport cancelled!");
            CooldownManager.removeCooldown(sender.getUniqueId(), CooldownType.CAMP);
            TimerManager.getInstance().getCooldownTimer().forcecancel(sender.getUniqueId(), "camp");
            warping.remove(sender.getName());
            cancel();
            return;
          }

          seconds--;
        }
      }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
  }
}