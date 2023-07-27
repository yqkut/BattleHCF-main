package cc.stormworth.hcf.util;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {

  public static String convertFirstUpperCase(String source) {
    return source.substring(0, 1).toUpperCase() + source.substring(1);
  }

  public static void removeOneItem(Player player) {
    if (player.getItemInHand().getAmount() > 1) {
      player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
      return;
    }

    player.getInventory().setItemInHand(new ItemStack(Material.AIR));
  }

  public static org.bukkit.inventory.ItemStack getSpawnerItem(int amount, EntityType type) {
    org.bukkit.inventory.ItemStack item = new ItemStack(Material.MOB_SPAWNER, amount);
    List<String> lore = new ArrayList<String>();

    String loreString = type.toString();
    loreString = loreString.substring(0, 1).toUpperCase() + loreString.substring(1).toLowerCase();
    loreString = loreString + " Spawner";
    lore.add(ChatColor.YELLOW + loreString);

    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(CC.GREEN + "Mob Spawner");
    meta.setLore(lore);
    item.setItemMeta(meta);

    return item;
  }

  public static <E> List<E> createList(final Object object, final Class<E> type) {
    final List<E> output = new ArrayList<>();
    if (object != null && object instanceof List) {
      final List<?> input = (List<?>) object;
      for (final Object value : input) {
        if (value != null) {
          if (value.getClass() == null) {
            continue;
          }
          if (!type.isAssignableFrom(value.getClass())) {
            final String simpleName = type.getSimpleName();
            throw new AssertionError(
                ("Cannot cast to list! Key " + value + " is not a " + simpleName));
          }
          final E e = type.cast(value);
          output.add(e);
        }
      }
    }
    return output;
  }

  public static ItemStack createSkull(String[] texture) {
    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    if (texture == null) {
      return head;
    } else {
      SkullMeta headMeta = (SkullMeta) head.getItemMeta();
      GameProfile profile = new GameProfile(UUID.randomUUID(), null);
      profile.getProperties().put("textures", new Property("textures", texture[0], texture[1]));

      try {
        Field profileField = headMeta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);
        profileField.set(headMeta, profile);
      } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException var5) {
        var5.printStackTrace();
      }

      head.setItemMeta(headMeta);
      return head;
    }
  }

  public static String getCardinalDirection(final Player player) {
    double rot = (player.getLocation().getYaw() - 90.0f) % 360.0f;
    if (rot < 0.0) {
      rot += 360.0;
    }
    return getDirection(rot);
  }

  private static String getDirection(final double rot) {
    if (0.0 <= rot && rot < 22.5) {
      return "W";
    }
    if (22.5 <= rot && rot < 67.5) {
      return "NW";
    }
    if (67.5 <= rot && rot < 112.5) {
      return "N";
    }
    if (112.5 <= rot && rot < 157.5) {
      return "NE";
    }
    if (157.5 <= rot && rot < 202.5) {
      return "E";
    }
    if (202.5 <= rot && rot < 247.5) {
      return "SE";
    }
    if (247.5 <= rot && rot < 292.5) {
      return "S";
    }
    if (292.5 <= rot && rot < 337.5) {
      return "SW";
    }
    if (337.5 <= rot && rot < 360.0) {
      return "W";
    }
    return null;
  }

  public static String getLocationName(Location location) {
    return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
  }

  public static void launchRandomFirework(Location loc) {
    Firework firework = loc.getWorld().spawn(loc,
        Firework.class);
    FireworkMeta fireworkMeta = (FireworkMeta) Bukkit.getItemFactory()
        .getItemMeta(Material.FIREWORK);

    fireworkMeta.setPower(2);

    FireworkEffect.Builder builder = FireworkEffect.builder();
    FireworkEffect.Type type = FireworkEffect.Type.values()[CorePlugin.RANDOM.nextInt(
        FireworkEffect.Type.values().length)];

    builder.with(type);

    for (int i = 0; i < 4; i++) {
      builder.withColor(
          Color.fromRGB(CorePlugin.RANDOM.nextInt(256), CorePlugin.RANDOM.nextInt(256),
              CorePlugin.RANDOM.nextInt(256)));
    }

    builder.withTrail().withFlicker();
    fireworkMeta.addEffect(builder.build());
    firework.setFireworkMeta(fireworkMeta);
  }

  public static boolean isEventLocated(Player player, boolean warzone) {
    if (!Main.getInstance().getMapHandler().isKitMap()) {
      boolean isWrongLocation =
          player.getWorld().getEnvironment() != World.Environment.NORMAL || Main.getInstance()
              .getServerHandler().isWarzone(player.getLocation());
      Team team = Main.getInstance().getTeamHandler().getTeam(player);
      if (team != null) {
        if (Main.getInstance().getEventHandler().getEvent("Citadel") != null && Main.getInstance()
            .getEventHandler().getEvent("Citadel").isActive()
            && Main.getInstance().getConquestHandler().getGame() != null) {
          if (warzone) {
            return isWrongLocation;
          }
          return true;
        }
      }
    }
    return false;
  }

  public static void removeThrownPearls(Player player) {
    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
      if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
        enderPearl.remove();
      }
    }
  }
}