package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Teleport;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class BowTeleporter extends DamageableAbility {

  public BowTeleporter() {
    super("BowTeleporter",
        "&6Bow Teleporter",
        Lists.newArrayList(
            "",
            "&7Shoot an enemy to teleport",
            "&7to him after 5 seconds.",
            ""
        ),
        new ItemStack(Material.BOW),
        TimeUtil.parseTimeLong("1m"));
  }

  @EventHandler
  public void onProjectileLaunch(ProjectileLaunchEvent event) {
    if (!(event.getEntity().getShooter() instanceof Player)) {
      return;
    }

    if (!(event.getEntity() instanceof Arrow)) {
      return;
    }

    Player player = (Player) event.getEntity().getShooter();

    if (isItem(player.getItemInHand())) {

      if (CooldownAPI.hasCooldown(player, getName())) {
        player.sendMessage(ChatColor.RED + "You have to wait " + TimeUtil.millisToRoundedTime(
            CooldownAPI.getCooldown(player, getName())) +
            " to use it again");
        event.setCancelled(true);
        player.getInventory().addItem(new ItemStack(Material.ARROW));
        return;
      }

      Arrow arrow = (Arrow) event.getEntity();
      arrow.setShooter(player);
      arrow.setMetadata("BowTeleporter", new FixedMetadataValue(Main.getInstance(), true));
    }
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Arrow)) {
      return;
    }

    Arrow arrow = (Arrow) event.getDamager();

    if (!arrow.hasMetadata("BowTeleporter")) {
      return;
    }

    if (!(arrow.getShooter() instanceof Player)) {
      return;
    }

    Player damager = (Player) arrow.getShooter();
    Player damaged = (Player) event.getEntity();

    if (damager.getLocation().distance(damaged.getLocation()) > 12) {
      damager.sendMessage(ChatColor.RED + "You are too far away to use this ability");
      CooldownAPI.setCooldown(damager, getName(), TimeUtil.parseTimeLong("12s"));
      return;
    }

    HCFProfile profile = HCFProfile.get(damager);
    Teleport teleport = new Teleport(damager, damager.getLocation(), damaged.getLocation(), 5);

    profile.setCountdown(teleport.start());

    teleport.setCancelledOnDamage(false);
    teleport.setCancelOnMove(false);
    teleport.setUuid(damaged.getUniqueId());
    teleport.setAbility(this);
    profile.setTeleport(teleport);

    CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");

    damager.sendMessage(CC.translate("&aStarting teleport to &e" + damaged.getName() + "&a..."));
    damager.sendMessage(CC.translate("&c&lDo not move or teleport will be canceled!"));

    super.onEntityDamageByEntity(event);
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}