package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class MedKit extends InteractAbility {

  public MedKit() {
    super("Immunity",
        "&cImmunity",
        Lists.newArrayList(
            "",
            "&7About to die? Use it to receive effects",
            "&7such as: Regeneration.",
            ""
        ),
        new ItemStack(Material.PAPER),
        TimeUtil.parseTimeLong("1m30s"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    getPotionEffects().forEach(effect -> Main.getInstance().getEffectRestorer().setRestoreEffect(player, effect));

    player.setMaxHealth(40);
    player.setHealth(40);

    TaskUtil.runLater(Main.getInstance(), () -> player.setMaxHealth(20), 20 * 5);

    super.onInteract(event);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event){
    if (event.getPlayer().getMaxHealth() > 20) {
      event.getPlayer().setMaxHealth(20);
    }
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return Lists.newArrayList(
        new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 4)
    );
  }
}