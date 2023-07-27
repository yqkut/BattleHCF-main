package cc.stormworth.hcf.misc.kits;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.listener.SpectatorListener;
import cc.stormworth.hcf.misc.kits.defaults.ArcherKit;
import cc.stormworth.hcf.misc.kits.defaults.BardKit;
import cc.stormworth.hcf.misc.kits.defaults.BuilderKit;
import cc.stormworth.hcf.misc.kits.defaults.DistanceArcherKit;
import cc.stormworth.hcf.misc.kits.defaults.DonorPremiumKit;
import cc.stormworth.hcf.misc.kits.defaults.PvPKit;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class KitManager {

  @Getter
  private static final Map<UUID, Long> lastClicked = Maps.newHashMap();
  @Getter
  private List<Kit> kits = Lists.newArrayList();

  public KitManager() {
    // load all kits from local redis
    CorePlugin.getInstance().runRedisCommand((redis) -> {
      for (String key : redis.keys("kit.*")) {
        Kit kit = CorePlugin.PLAIN_GSON.fromJson(redis.get(key), Kit.class);

        kits.add(kit);
      }
      return null;
    });

    // load default kits
    if (kits.isEmpty()) {
      kits = Arrays.asList(
          new PvPKit(),
          new BardKit(),
          new ArcherKit(),
          new DistanceArcherKit(),
          new BuilderKit(),
          new DonorPremiumKit()
      );
    }

    // sort kits by name, alphabetically
    kits.sort((first, second) -> first.getName().compareToIgnoreCase(second.getName()));

    // We have to do this later to 'steal' priority
    Bukkit.getScheduler().runTaskLater(Main.getInstance(),
        () -> rCommandHandler.registerParameterType(Kit.class, new Kit.Type()), 5L);
    new KitsNPC();
  }

  public static void attemptApplyKit(Player player, Kit kit) {
    if (kit == null) {
      player.sendMessage(ChatColor.RED + "Unknown kit.");
      return;
    }

    if (CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)
        || SpectatorListener.spectators.contains(player.getUniqueId())) {
      player.sendMessage(ChatColor.RED + "You cannot use this at this moment.");
      return;
    }

    if (!CustomTimerCreateCommand.sotwday && player.getGameMode() != GameMode.CREATIVE
        && lastClicked.containsKey(player.getUniqueId()) && (
        System.currentTimeMillis() - lastClicked.get(player.getUniqueId())
            < TimeUnit.SECONDS.toMillis(15))) {
      player.sendMessage(ChatColor.RED + "Please wait before using this again.");
      return;
    }

    if (!Main.getInstance().getMapHandler().getKitManager().canUseKit(player, kit.getName())) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&',
          "&cYou don't own this kit. Purchase it at store.battle.rip."));
      return;
    }

        /*if (kit.getName().equalsIgnoreCase("archer")) {
            int playtimeTime = (int) Main.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId());
            final Player bukkitPlayer = Main.getInstance().getServer().getPlayer(player.getUniqueId());
            if (bukkitPlayer != null) {
                playtimeTime += (int) (Main.getInstance().getPlaytimeMap().getCurrentSession(bukkitPlayer.getUniqueId()) / 1000L);
            }
            if (playtimeTime < 900) {
                player.sendMessage(CC.RED + "You must have minimum 15 minutes of playtime to grab archer kit.");
                player.sendMessage(CC.YELLOW + "Your playtime is " + CC.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(playtimeTime) + CC.YELLOW + ".");
                return;
            }
        }*/

    kit.apply(player);

    if (!CustomTimerCreateCommand.sotwday) {
      lastClicked.put(player.getUniqueId(), System.currentTimeMillis());
    }
  }

  public Kit get(String name) {
    for (Kit kit : kits) {
      if (kit.getName().equalsIgnoreCase(name)) {
        return kit;
      }
    }

    return null;
  }

  public Kit getOrCreate(String name) {
    for (Kit kit : kits) {
      if (kit.getName().equalsIgnoreCase(name)) {
        return kit;
      }
    }

    Kit kit = new Kit(name);
    kits.add(kit);

    return kit;
  }

  public void delete(Kit kit) {
    kits.remove(kit);
  }

  public void save() {
    CorePlugin.getInstance().runRedisCommand((redis) -> {
      for (Kit kit : kits) {
        redis.set("kit." + kit.getName(), CorePlugin.PLAIN_GSON.toJson(kit));
      }
      return null;
    });
  }

  public boolean canUseKit(Player player, String kitName) {
    // You can always use these kits
    if (kitName.equals("Duelist")
        || kitName.equals("Miner")
        || kitName.equals("Builder")
        || kitName.equals("PvP")
        || kitName.equals("Archer")
        || kitName.equals("Bard")
        || kitName.equals("Rogue")) {
      return true;
    }

    return player.hasPermission("crazyenchantments.gkitz." + kitName.toLowerCase());
  }
}