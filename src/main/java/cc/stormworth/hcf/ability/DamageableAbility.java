package cc.stormworth.hcf.ability;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class DamageableAbility extends Ability {

  public DamageableAbility(String name, String displayName, List<String> description,
      ItemStack item, long cooldown) {
    super(name, displayName, description, item, cooldown);
  }

  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    Player damager = null;
    if (!(event.getDamager() instanceof Player)) {
      if (event.getDamager() instanceof Projectile) {
        Projectile projectile = (Projectile) event.getDamager();
        if (projectile.getShooter() instanceof Player) {
          damager = (Player) projectile.getShooter();
        }
      }
    } else {
      damager = (Player) event.getDamager();
    }

    if (damager == null) {
      return;
    }

    Player victim = (Player) event.getEntity();
    sendActivation(damager);
    victim.sendMessage(CC.translate("&cYou have been hit with &f" + this.getDisplayName()));
    CooldownManager.sendCooldown(damager.getUniqueId(), getLunarcooldown());
    consume(damager);
    CooldownAPI.setCooldown(damager, "Global", TimeUtil.parseTimeLong("10s"));
  }

}