package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Hit;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class Antitrapper extends DamageableAbility {

  public Antitrapper() {
    super("Antitrapper",
        "&9Antitrapper",
        Lists.newArrayList(
            "",
            "&7Hit your target &c3 times &7to prevent",
            "&7him from not being able to",
            "&7build in the next 30 seconds,",
            ""
        ),
        new ItemStack(Material.BONE),
        TimeUtil.parseTimeLong("1m30s"));
  }

  @Override
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    Player damager = (Player) event.getDamager();
    Player damaged = (Player) event.getEntity();

    HCFProfile profileDamager = HCFProfile.get(damager);

    Hit hit = profileDamager.getHit();

    if (hit == null || hit.getUuid() != damaged.getUniqueId() || !isItem(hit.getItemStack())) {
      hit = new Hit(damaged.getUniqueId(), getItem());
      profileDamager.setHit(hit);
    }

    hit.setHits(hit.getHits() + 1);

    if ((3 - hit.getHits()) > 0) {
      damager.sendMessage(
          CC.translate("&6&l[&e✷&6&l] &eYou have to hit your enemy &e&l" + (3 - hit.getHits())
              + " more times."));
    }

    if (hit.getHits() < 3) {
      return;
    }

    CooldownAPI.setCooldown(damaged, "Trapped", TimeUtil.parseTimeLong("16s"));
    CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
    hit.setHits(0);

    damaged.sendMessage("");
    damaged.sendMessage(CC.translate("&cYou have been trapped, you can not place blocks or put it for 16 seconds"));
    damaged.sendMessage("");
    super.onEntityDamageByEntity(event);
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

    if (CooldownAPI.hasCooldown(player, "Trapped")) {
      player.sendMessage(ChatColor.RED + "You have to wait " +
          TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "Trapped")) +
          " to interact again");
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBuild(BlockBreakEvent event) {
    Player player = event.getPlayer();

    if (CooldownAPI.hasCooldown(player, "Trapped")) {
      player.sendMessage(ChatColor.RED + "You have to wait " +
          TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "Trapped")) +
          " to build again");
      event.setCancelled(true);
    }
  }


  @EventHandler
  public void onBuild(BlockPlaceEvent event) {
    Player player = event.getPlayer();

    if (CooldownAPI.hasCooldown(player, "Trapped")) {
      player.sendMessage(ChatColor.RED + "You have to wait " +
          TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(player, "Trapped")) +
          " to build again");
      event.setCancelled(true);
    }
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}