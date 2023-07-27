package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.InteractAbility;
import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

public class Dash extends InteractAbility {

  public Dash() {
    super("Dash",
        "&aDash",
        Lists.newArrayList(
            "",
            "&7Impulse yourself &a6 blocks",
            "&7blocks forward with a short cooldown.",
            ""
        ),
        new ItemBuilder(Material.FEATHER).build(),
        TimeUtil.parseTimeLong("10s"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (event.getClickedBlock() != null) {
      Block block = event.getClickedBlock();
      if (block.getType().name().contains("SIGN") || block.getType().name().contains("WALL_SIGN")) {
        Sign sign = (Sign) block.getState();
        if (sign.getLine(0).equalsIgnoreCase(CC.translate("&9[Elevator]"))) {
          return;
        }
      }
    }

    player.setVelocity(player.getLocation().getDirection().multiply(5.5).setY(0.1));

    super.onInteract(event);
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}