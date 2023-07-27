package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class KnifesGun extends InteractAbility {

  public KnifesGun() {
    super("Gun",
        "&7Gun",
        Lists.newArrayList(
            "",
            "Shoots: 5",
            "",
            "&7Good at CounterStrike or any shooter? ",
            "&7Shoot your enemy to take his life, ",
            "&7try to aim at his head to give him negative effects",
            ""
        ),
        new ItemStack(Material.GOLD_HOE),
        TimeUtil.parseTimeLong("1m30s"));
  }

  @Override
  public boolean isItem(ItemStack item) {
    return item != null &&
        item.getType() == getItemOriginal().getType() &&
        item.hasItemMeta() &&
        item.getItemMeta().hasDisplayName() &&
        item.getItemMeta().getDisplayName().equalsIgnoreCase(getItemOriginal().getItemMeta().getDisplayName()) &&
        (item.getItemMeta().hasLore());
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    List<String> lore = player.getItemInHand().getItemMeta().getLore();

    if (!lore.get(1).contains("Shoots:")) {
      return;
    }

    if (CooldownAPI.hasCooldown(player, "Snowball-Shoot")) {
      player.sendMessage(ChatColor.RED + "You have to wait " + TimeUtil.millisToRoundedTime(
          CooldownAPI.getCooldown(player, "Snowball-Shoot")) +
          " to use it again");
      return;
    }

    int currentShots = Integer.parseInt(lore.get(1).split(":")[1].trim());

    if (currentShots <= 0) {

      ItemMeta meta = player.getItemInHand().getItemMeta();
      lore.removeIf(s -> s.contains("Shoots: "));
      meta.setLore(lore);
      player.getItemInHand().setItemMeta(meta);

      player.sendMessage(ChatColor.RED + "You have no more shots");
      super.onInteract(event);
      return;
    }

    lore.set(1, "Shoots: " + (currentShots - 1));
    ItemMeta meta = player.getItemInHand().getItemMeta();
    meta.setLore(lore);
    player.getItemInHand().setItemMeta(meta);

    Snowball snowball = player.launchProjectile(Snowball.class);
    snowball.setShooter(player);
    snowball.setMetadata("knifesgun", new FixedMetadataValue(Main.getInstance(), true));

    CooldownAPI.setCooldown(player, "Snowball-Shoot", TimeUtil.parseTimeLong("1s"));
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
      if (event.getDamager().hasMetadata("knifesgun")) {

        Snowball snowball = (Snowball) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if (DTRBitmask.SAFE_ZONE.appliesAt(damaged.getLocation())) {
          handleAbilityRefund(damaged,
              CC.RED + "You cannot use special items against players in spawn.", false);
          event.setCancelled(true);
          return;
        }

        if (isHeadshot(snowball, damaged)) {
          damaged.setHealth(damaged.getHealth() - 5);
          damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 3, 1));
          damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1));
        } else {
          damaged.setHealth(damaged.getHealth() - 2);
        }
      }
    }
  }

  public boolean isHeadshot(Projectile projectile, Player player) {
    double y = projectile.getLocation().getY();
    double y2 = player.getEyeLocation().getY();
    double distance = Math.abs(y - y2);
    return distance <= 0.5D;
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}