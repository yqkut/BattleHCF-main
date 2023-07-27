package cc.stormworth.hcf.deathmessage.objects;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public abstract class MobDamage extends Damage {

  private final EntityType mobType;

  public MobDamage(final Player damaged, final double damage, final EntityType mobType) {
    super(damaged, damage);
    this.mobType = mobType;
  }

  public EntityType getMobType() {
    return this.mobType;
  }
}