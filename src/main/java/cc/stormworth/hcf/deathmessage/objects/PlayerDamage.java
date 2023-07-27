package cc.stormworth.hcf.deathmessage.objects;

import org.bukkit.entity.Player;

public abstract class PlayerDamage extends Damage {

  private final Player damager;

  public PlayerDamage(final Player damaged, final double damage, final Player damager) {
    super(damaged, damage);
    this.damager = damager;
  }

  public Player getDamager() {
    return this.damager;
  }
}