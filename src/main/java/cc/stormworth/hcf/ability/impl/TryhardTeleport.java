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
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class TryhardTeleport extends InteractAbility {

  public TryhardTeleport() {
    super("TryhardTeleport", "&eTryhardTeleport",
        Lists.newArrayList(
            "&7Do you usually play aggressively? Throw this to",
            "&7your enemy to teleport to him and receive strength,",
            "&7he will receive antitrapper",
            ""
        ),
        new ItemStack(Material.SNOW_BALL),
        TimeUtil.parseTimeLong("32s"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    event.setCancelled(true);

    Projectile projectile;

    if (Main.getInstance().getMapHandler().isKitMap()){
      projectile = player.launchProjectile(Egg.class);
    }else{
      projectile = player.launchProjectile(Snowball.class);
    }

    projectile.setShooter(player);
    projectile.setMetadata("EggPort", new FixedMetadataValue(Main.getInstance(), true));

    sendActivation(event.getPlayer());
    event.setCancelled(true);
    consume(event.getPlayer());
    CooldownAPI.setCooldown(event.getPlayer(), "Global", TimeUtil.parseTimeLong("10s"));
  }

  public boolean isItem(ItemStack item) {
    ItemStack abilityItem = getItemOriginal();

    if (item == null) return false;

    if (item.getType() != abilityItem.getType() && item.getType() != Material.EGG) return false;

    if (!item.hasItemMeta()) return false;

    ItemMeta abilityMeta = abilityItem.getItemMeta();
    ItemMeta itemMeta = item.getItemMeta();

    return itemMeta.hasDisplayName() && itemMeta.getDisplayName().equalsIgnoreCase(abilityMeta.getDisplayName()) &&
            (itemMeta.hasLore() == abilityMeta.hasLore()) && itemMeta.getLore().equals(abilityMeta.getLore());
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Projectile)) {
      return;
    }

    Projectile egg = (Projectile) event.getDamager();

    if (!egg.hasMetadata("EggPort")) {
      return;
    }

    Player damager = (Player) egg.getShooter();
    Player damaged = (Player) event.getEntity();

    CooldownAPI.removeCooldown(damager, getName());

    if (damager.getLocation().distance(damaged.getLocation()) > 12) {
      damager.sendMessage(ChatColor.RED + "You are too far away to use this ability");
      CooldownAPI.setCooldown(damager, getName(), TimeUtil.parseTimeLong("12s"));
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

    damager.teleport(damaged.getLocation());
    getPotionEffects().forEach(damager::addPotionEffect);

    CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
    CooldownAPI.setCooldown(damaged, "Trapped", TimeUtil.parseTimeLong("5s"));
    
    for (Entity entity : damaged.getNearbyEntities(5.0D, 5.0D, 5.0D)) {
    	if (entity instanceof Player) {
    		((Player) entity).sendMessage(CC.translate("&6" + damager.getName() + " &ehas used the &cTryhard &eability!"));
    	}
    }
    
    damaged.sendMessage(CC.translate("&cYou have been trapped, you can not place blocks or put it for 5 seconds"));
  }

  @Override
  public void handleAbilityRefund(Player player, String message, boolean returnItem) {
    if (returnItem) {

      if (Main.getInstance().getMapHandler().isKitMap()){
        ItemStack item = getItem().clone();
        item.setType(Material.EGG);
        player.getInventory().addItem(item);
      }else{
        player.getInventory().addItem(this.getItem().clone());
      }

      player.updateInventory();
    }
    if (message != null) {
      player.sendMessage(message);
    }

    CooldownAPI.removeCooldown(player, getName());
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return Lists.newArrayList(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1)
    );
  }
}