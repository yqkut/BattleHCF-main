package cc.stormworth.hcf.ability.impl.blinder;

import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.InteractAbility;
import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Blinder extends InteractAbility {

  public Blinder() {
    super("Blinder",
        "&dBlinder",
        Lists.newArrayList(
            "",
            "&7Flashbang! Oh sh*t, it wasn't like that,",
            "&7but use it to blind enemies that see it passing by.",
            ""
        ),
        new ItemStack(Material.EGG),
        TimeUtil.parseTimeLong("1m"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    Egg egg = player.launchProjectile(Egg.class);
    egg.setShooter(player);
    BlinderRunnable.getEntities().add(egg);
    super.onInteract(event);
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof Egg)) {
      return;
    }

    Egg egg = (Egg) event.getEntity();
    if (!BlinderRunnable.getEntities().contains(egg)) {
      return;
    }

    BlinderRunnable.getEntities().remove(egg);
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}