package cc.stormworth.hcf.deathmessage.util;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.deathmessage.objects.Damage;
import org.bukkit.entity.Player;

public class UnknownDamage extends Damage {

  public UnknownDamage(final Player damaged, final double damage) {
    super(damaged, damage);
  }

  @Override
  public Clickable getDeathMessage() {
    Clickable clickable = getHoverStats(this.getDamaged());

    clickable.add(CC.translate(" &edied."));

    return clickable;
  }
}