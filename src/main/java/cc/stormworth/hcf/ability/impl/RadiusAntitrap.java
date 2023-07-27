package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RadiusAntitrap extends InteractAbility {

  public RadiusAntitrap() {
    super("RadiusAntitrap",
        "&9RadiusAntitrap",
        Lists.newArrayList(
            "",
            "&7Don't get caught! Use it to prevent",
            "&7your enemies from placing blocks for a while.",
            ""
        ),
        new ItemStack(Material.NOTE_BLOCK),
        TimeUtil.parseTimeLong("2m30s"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      player.sendMessage(
          CC.translate("&cYou can only activate the &9RadiusAntitrap&c by setting it as a block."));
      return;
    }

    player.getNearbyEntities(10, 10, 10)
        .stream()
        .filter(entity -> entity instanceof Player)
        .forEach(entity -> {
          Player target = (Player) entity;
          CooldownAPI.setCooldown(target, "Trapped2", TimeUtil.parseTimeLong("15s"));
          CooldownManager.sendCooldown(target.getUniqueId(),
              new LCCooldown(CC.translate(getDisplayName()), (int) getCooldown(),
                  TimeUnit.MILLISECONDS, getItem().getType()));

          target.sendMessage(
              CC.translate("&6&l[&e✷&6&l] &eA &6&lRadius Antitrapper &ehas been placed nearby."));

          target.sendMessage(
              CC.translate("&8[&c⚠&8]&c You cannot place or break block until it is destroyed."));
        });

    player.setMetadata("RadiusAntitrap", new FixedMetadataValue(Main.getInstance(), true));
    //super.onInteract(event);

    event.setCancelled(false);
  }


  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();

    if (!player.hasMetadata("RadiusAntitrap")) {
      return;
    }

    player.removeMetadata("RadiusAntitrap", Main.getInstance());

    Block block = event.getBlock();

    if (event.getBlock().getType() != Material.NOTE_BLOCK) {
      return;
    }

    Block below = block.getRelative(BlockFace.DOWN);

    if (below == null || below.getType() == Material.AIR) {
      player.sendMessage(CC.translate("&cYou cannot put the &6&lRadius Antitrapper here."));
      event.setCancelled(true);
      return;
    }

    block.setMetadata("trap", new FixedMetadataValue(Main.getInstance(), true));

    event.setCancelled(false);

    CooldownAPI.setCooldown(event.getPlayer(), getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
    sendActivation(event.getPlayer());
    consume(event.getPlayer());
    CooldownAPI.setCooldown(event.getPlayer(), "Global", TimeUtil.parseTimeLong("10s"));
    player.sendMessage(CC.translate("&aYou have activated &bRadiusAntitrap&a!"));

    TaskUtil.runLater(Main.getInstance(), () -> {
      if (block.hasMetadata("trap")) {
        block.removeMetadata("trap", Main.getInstance());
        block.removeMetadata("hits", Main.getInstance());
        block.setType(Material.AIR);
      }
    }, 20 * 15);
  }

  @EventHandler
  public void onBreak(BlockBreakEvent event) {

    Block block = event.getBlock();

    if (block.hasMetadata("trap")) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onHitTrap(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }

    Block block = event.getClickedBlock();

    if (!block.hasMetadata("trap")) {
      return;
    }

    int hit = 0;

    if (block.hasMetadata("hits")) {
      hit = block.getMetadata("hits").get(0).asInt();
    }

    hit++;

    if (hit > 20) {
      block.removeMetadata("trap", Main.getInstance());
      CooldownAPI.removeCooldown(player, "Trapped2");
      player.getNearbyEntities(10, 10, 10)
          .stream()
          .filter(entity -> entity instanceof Player)
          .forEach(entity -> {
            Player target = (Player) entity;
            CooldownAPI.removeCooldown(target, "Trapped2");

            player.sendMessage(
                CC.translate("&6&l[&e✷&6&l] &eA &6&lRadius Antitrapper &ehas been destroyed."));
          });

      block.removeMetadata("trap", Main.getInstance());
      block.removeMetadata("hits", Main.getInstance());
      block.setType(Material.AIR);
      return;
    }

    player.sendMessage(CC.translate("&6&l[&e✷&6&l] &eHits left to destroy: &6&l" + (20 - hit)));
    block.setMetadata("hits", new FixedMetadataValue(Main.getInstance(), hit));
  }

  @EventHandler
  public void onInteractTrapped(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    Block block = event.getClickedBlock();

    if (block == null) {
      return;
    }

    if (!block.getType().name().contains("GATE") &&
        !block.getType().name().contains("FENCE") &&
        !block.getType().name().contains("LEVER") &&
        !block.getType().name().contains("BUTTON") &&
        !block.getType().name().contains("PLATE") &&
        !block.getType().name().contains("DOOR")) {
      return;
    }

    if (CooldownAPI.hasCooldown(player, "Trapped2")) {
      player.sendMessage(ChatColor.RED + "You have to wait " +
          TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "Trapped2")) +
          " to interact again");
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBuild(BlockPlaceEvent event) {
    Player player = event.getPlayer();

    if (CooldownAPI.hasCooldown(player, "Trapped2")) {
      player.sendMessage(ChatColor.RED + "You have to wait " +
          TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "Trapped2")) +
          " to build again");
      event.setCancelled(true);
    }
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}