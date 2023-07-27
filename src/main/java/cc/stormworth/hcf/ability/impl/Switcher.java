package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class Switcher extends InteractAbility {

  public Switcher() {
    super("Switcher",
        "&bSwitcher",
        Lists.newArrayList(
            "",
            "&7Swap positions with your",
            "&7target in a range of &f9 blocks&7.",
            ""
        ),
        new ItemStack(Material.SNOW_BALL),
        TimeUtil.parseTimeLong("24s"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    event.setCancelled(true);

    Snowball snowball = player.launchProjectile(Snowball.class);
    snowball.setShooter(player);
    snowball.setMetadata("Switcher", new FixedMetadataValue(Main.getInstance(), true));
    super.onInteract(event);
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    if (!(event.getDamager() instanceof Snowball)) {
      return;
    }

    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Snowball snowball = (Snowball) event.getDamager();

    Player damager = (Player) snowball.getShooter();
    Player damaged = (Player) event.getEntity();

    if (!snowball.hasMetadata("Switcher")) {
      return;
    }

    CooldownAPI.removeCooldown(damager, getName());

    if (CooldownAPI.hasCooldown(damager, getName())) {
      damager.sendMessage(ChatColor.RED + "You have to wait " + TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(damager, getName())) + " to use it again");
      return;
    }

    if (DTRBitmask.SAFE_ZONE.appliesAt(damaged.getLocation())) {
      handleAbilityRefund(damager, CC.RED + "You cannot use special items against players in spawn.", true);
      event.setCancelled(true);
      return;
    }

    HCFProfile profile = HCFProfile.get(damaged);

    if (profile.hasPvPTimer()) {
      handleAbilityRefund(damager, CC.RED + "You cannot use special items against players with pvp timer.", true);
      event.setCancelled(true);
      return;
    }

    if (damager.getLocation().distance(damaged.getLocation()) > 12) {
      damager.sendMessage(ChatColor.RED + "You are too far away to use this ability");
      CooldownAPI.setCooldown(damager, getName(), TimeUtil.parseTimeLong("12s"));
      return;
    }

    Location damagerLocation = damager.getLocation();
    Location damagedLocation = damaged.getLocation();

    damager.teleport(damagedLocation);
    damaged.teleport(damagerLocation);
    CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}