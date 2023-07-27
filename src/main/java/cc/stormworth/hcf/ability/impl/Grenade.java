package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;


public class Grenade extends InteractAbility {

  public Grenade() {
    super("Grenade",
        "&7Grenade",
        Lists.newArrayList(
            "",
            "&7Explosive projectile that when",
            "&7exploding on the ground will give",
            "&7positive effects to allies and negative",
            "&7effects to enemies, but be careful that it doesn't explode in your hand, huh!",
            ""
        ),
        new ItemBuilder(Material.EGG).build(),
        TimeUtil.parseTimeLong("1m30s"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    Egg egg = player.launchProjectile(Egg.class);
    egg.setShooter(player);
    egg.setMetadata("RageBall", new FixedMetadataValue(Main.getInstance(), true));

    super.onInteract(event);
  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event)
  {
    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG)
    {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof Egg)) {
      return;
    }

    Egg egg = (Egg) event.getEntity();

    if (!egg.hasMetadata("RageBall")) {
      return;
    }

    if (!(egg.getShooter() instanceof Player)) {
      return;
    }

    Player player = (Player) egg.getShooter();

    List<Player> allies = Lists.newArrayList(player);
    List<Player> enemies = Lists.newArrayList();

    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    for (Entity entity : egg.getNearbyEntities(5, 20, 5)) {

      if (!(entity instanceof Player)) {
        continue;
      }

      if (entity == player) {
        continue;
      }

      Player target = (Player) entity;

      if (DTRBitmask.SAFE_ZONE.appliesAt(target.getLocation())) {
        continue;
      }

      HCFProfile profile = HCFProfile.get(target);

      if (profile.hasPvPTimer()) {
        continue;
      }

      Team targetTeam = Main.getInstance().getTeamHandler().getTeam(target);

      if (team == null) {
        enemies.add(target);
      } else if (team.isMember(target.getUniqueId())) {
        allies.add(target);
      } else if (targetTeam == null) {
        enemies.add(target);
      } else if (team.getAllies().contains(targetTeam.getUniqueId())) {
        allies.add(target);
      } else {
        enemies.add(target);
      }

      target.setMetadata("grenade", new FixedMetadataValue(Main.getInstance(), true));
    }

    for (Player other : allies) {
      other.sendMessage(CC.translate("&6&l[&e✷&6&l] &eA &6&lGrenade &ewas thrown close to you."));
      other.sendMessage(CC.translate("&8[&a✷&7] &aYou receive healing effects from you team."));
      getPotionEffects().forEach(potionEffect -> {
        Main.getInstance().getEffectRestorer().setRestoreEffect(other, potionEffect);
      });
    }

    for (Player other : enemies) {
      other.sendMessage(CC.translate("&6&l[&e✷&6&l] &eA &6&lGrenade &ewas thrown close to you."));
      other.sendMessage(
          CC.translate("&8[&c⚠&8]&c You received negative effects for 5 seconds."));
      getNegativePotionEffects().forEach(potionEffect -> other.addPotionEffect(potionEffect, true));
    }
    Location location = egg.getLocation();

    location.getWorld().createExplosion(location, 2F, false);
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (event.getCause() == DamageCause.BLOCK_EXPLOSION
        || event.getCause() == DamageCause.ENTITY_EXPLOSION) {
      Player player = (Player) event.getEntity();

      if (!player.hasMetadata("grenade")) {
        return;
      }

      event.setCancelled(true);
      player.removeMetadata("grenade", Main.getInstance());
    }
  }

  public List<PotionEffect> getNegativePotionEffects() {
    return Lists.newArrayList(
        new PotionEffect(PotionEffectType.WEAKNESS, 20 * 6, 1),
        new PotionEffect(PotionEffectType.SLOW, 20 * 6, 1)
    );
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return Lists.newArrayList(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 1),
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 1)
    );
  }
}