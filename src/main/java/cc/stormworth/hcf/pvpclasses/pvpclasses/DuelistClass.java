package cc.stormworth.hcf.pvpclasses.pvpclasses;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.RestoreEffect;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class DuelistClass extends PvPClass {

  private final Map<UUID, Integer> uses = Maps.newHashMap();

  public DuelistClass() {
    super("Duelist", Arrays.asList(Material.BLAZE_POWDER, Material.DIAMOND_HOE), 2);
  }

  @Override
  public void apply(final Player player) {
    super.apply(player);
    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
        Integer.MAX_VALUE, 1), true);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
  }

  @Override
  public void tick(final Player player) {
    if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
      player.addPotionEffect(
          new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2));
    }
    if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
      player.addPotionEffect(
          new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    super.tick(player);
  }

  @Override
  public void remove(Player player) {
    super.remove(player);

    Map<Effect, RestoreEffect> entry = Main.getInstance().getEffectRestorer().getRestores().row(player.getUniqueId());

    for (Iterator<RestoreEffect> it = entry.values().iterator(); it.hasNext();) {
      RestoreEffect restore = it.next();
      PotionEffect potionEffect = restore.getEffect();

      if (potionEffect.getType().getName().equals("SPEED") && potionEffect.getAmplifier() == 1) {
        it.remove();
      } else if (potionEffect.getType().getName().equals("FAST_DIGGING") && potionEffect.getAmplifier() == 2) {
        it.remove();
      }
    }


    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
      if(activePotionEffect.getAmplifier() == 1 && activePotionEffect.getType().equals(PotionEffectType.SPEED)) {
        player.removePotionEffect(activePotionEffect.getType());
      }

      if(activePotionEffect.getAmplifier() == 2 && activePotionEffect.getType().equals(PotionEffectType.FAST_DIGGING)) {
        player.removePotionEffect(activePotionEffect.getType());
      }
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack item = event.getItem();

    if (item == null || item.getType() == Material.AIR) {
      return;
    }

    if (item.getType() == Material.DIAMOND_HOE) {
      if (CooldownAPI.hasCooldown(player, "duelist_diamond_hoe")) {
        player.sendMessage(CC.translate("&cYou must wait &e" +
            TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "duelist_diamond_hoe"))
            + " &cto use this item."));
        return;
      }

      if (!PvPClassHandler.hasKitOn(player, this)) {
        return;
      }

      if (CooldownAPI.hasCooldown(event.getPlayer(), "anti_class")) {
        return;
      }

      if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
        player.sendMessage(ChatColor.RED + "You cannot use this in spawn!");
        return;
      }

      if (uses.containsKey(player.getUniqueId())) {
        if (uses.get(player.getUniqueId()) >= 2) {
          CooldownAPI.setCooldown(player, "duelist_diamond_hoe", TimeUtil.parseTimeLong("30s"));
          uses.put(player.getUniqueId(), 0);
          return;
        }
      }

      Snowball snowball = player.launchProjectile(Snowball.class);
      snowball.setShooter(player);
      snowball.setMetadata("duelist", new FixedMetadataValue(Main.getInstance(), true));
      uses.put(player.getUniqueId(), uses.getOrDefault(player.getUniqueId(), 0) + 1);
    }
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
      if (event.getDamager().hasMetadata("duelist")) {

        Snowball snowball = (Snowball) event.getDamager();
        Player damaged = (Player) event.getEntity();
        Player shooter = (Player) snowball.getShooter();

        damaged.setHealth(damaged.getHealth() - 2);

        shooter.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 4 * 20, 0));
        shooter.teleport(damaged.getLocation());
      }
    }
  }

  @Override
  public boolean itemConsumed(final Player player, final Material material) {
    if (material == Material.BLAZE_POWDER) {
      if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
        player.sendMessage(ChatColor.RED + "You cannot use this in spawn!");
        return false;
      }
      if (CooldownAPI.hasCooldown(player, "duelist_blaze_rod")) {
        player.sendMessage(CC.translate("&cYou must wait &e" +
            TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "duelist_blaze_rod"))
            + " &cto use this again."));
        return false;
      }

      CooldownAPI.setCooldown(player, "duelist_blaze_rod", TimeUtil.parseTimeLong("25s"));

      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 3), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 0), true);

      SpawnTagHandler.addPassiveSeconds(player, SpawnTagHandler.getMaxTagTime());
      return true;
    }

    return false;
  }


  @Override
  public boolean qualifies(PlayerInventory armor) {
    return this.wearingAllArmor(armor) &&
        armor.getHelmet().getType() == Material.CHAINMAIL_HELMET
        && armor.getChestplate().getType() == Material.DIAMOND_CHESTPLATE
        && armor.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS
        && armor.getBoots().getType() == Material.DIAMOND_BOOTS;
  }
}