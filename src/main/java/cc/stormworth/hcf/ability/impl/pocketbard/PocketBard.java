package cc.stormworth.hcf.ability.impl.pocketbard;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.InteractAbility;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class PocketBard extends InteractAbility {

  public PocketBard() {
    super("PocketBard",
        "&6Pocket Bard",
        Lists.newArrayList(
            "",
            "&7Choose any of the following &ebard &7effects.",
            "",
            "&eAmount&f: &fx3",
            ""
        ),
        new ItemBuilder(Material.INK_SACK)
            .data((short) 14)
            .build(),
        TimeUtil.parseTimeLong("1m"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    new PocketBardMenu().openMenu(event.getPlayer());
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}