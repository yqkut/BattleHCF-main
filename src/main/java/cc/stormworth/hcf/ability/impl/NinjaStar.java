package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Teleport;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class NinjaStar extends InteractAbility {

  public NinjaStar() {
    super("NinjaStar",
        "&bNinjaStar",
        Lists.newArrayList(
            "",
            "&7Allows you to teleport to the",
            "&7player who has hit you in",
            "&7the last 15 seconds.",
            ""
        ),
        new ItemStack(Material.NETHER_STAR),
        TimeUtil.parseTimeLong("1m45s"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    HCFProfile profile = HCFProfile.get(player);
    Player target = profile.getLastDamager();

    if (target == null) {
      player.sendMessage(
          CC.RED + "You must have been damaged by someone before using this ability.");
      return;
    }

    if (profile.isExpiredDamager()) {
      profile.setLastDamager(null);
      player.sendMessage(
          CC.RED + "You must have been damaged by someone before using this ability.");
      return;
    }

    player.sendMessage(CC.translate(
        "&aYou have just used a &b&lNinja Star&a. You will be teleported in 5 seconds."));
    target.sendMessage(CC.translate(player.getDisplayName()
        + " &chas used a &b&lNinja Star &cand it will be teleported to you in 5 seconds."));

    Teleport teleport = new Teleport(player, player.getLocation(), target.getLocation(), 3);

    teleport.setCancelOnMove(false);
    teleport.setCancelledOnDamage(false);
    teleport.setShowParticles(false);

    teleport.setAbility(this);

    teleport.setOnTeleport((other) -> {
      profile.setLastDamager(null);
      getPotionEffects().forEach(other::addPotionEffect);
    });

    teleport.setUuid(target.getUniqueId());

    profile.setTeleport(teleport);
    profile.setCountdown(teleport.start());

    super.onInteract(event);
  }


  @Override
  public List<PotionEffect> getPotionEffects() {
    return Lists.newArrayList(
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1)
    );
  }
}