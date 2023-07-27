package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Regeneration extends InteractAbility {

  public Regeneration() {
    super("Regeneration",
        "&dRegeneration III",
        Lists.newArrayList(
            "",
            "&7Give yourself and your allies &dRegeneration III",
            "&7for 5 seconds in a radius of 20 blocks",
            ""
        ),
        new ItemStack(Material.GHAST_TEAR),
        TimeUtil.parseTimeLong("2m"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {

    Player player = event.getPlayer();

    getPotionEffects().forEach(effect -> Main.getInstance().getEffectRestorer().setRestoreEffect(player, effect));

    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    if (team != null) {
      player.getNearbyEntities(20, 20, 20).stream()
          .filter(entity -> entity instanceof Player)
          .map(entity -> (Player) entity)
          .forEach(other -> {
            if (team.isMember(other.getUniqueId())) {
              getPotionEffects().forEach(effect -> Main.getInstance().getEffectRestorer().setRestoreEffect(other, effect));
            }
          });
    }

    super.onInteract(event);
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return Lists.newArrayList(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2));
  }
}