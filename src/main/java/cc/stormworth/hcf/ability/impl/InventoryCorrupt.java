package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.List;

public class InventoryCorrupt extends DamageableAbility {

  public InventoryCorrupt() {
    super("InventoryCorrupt",
        "&9InventoryCorrupt",
        Lists.newArrayList(
            "",
            "&7Drive your enemy crazy with this ability,",
            "&7change his entire inventory by hitting him with it..",
            ""
        ),
        new ItemStack(Material.POISONOUS_POTATO),
        TimeUtil.parseTimeLong("45s"));
  }

  @Override
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    Player damager = (Player) event.getDamager();
    Player damaged = (Player) event.getEntity();

    List<ItemStack> hotbar = Lists.newArrayList();

    for (int i = 0; i < 9; i++) {
      ItemStack itemStack = damaged.getInventory().getItem(i);

      if (itemStack == null) {
        hotbar.add(new ItemStack(Material.AIR));
      } else {
        hotbar.add(itemStack);
      }
    }

    Collections.shuffle(hotbar);

    ItemStack[] array = hotbar.toArray(new ItemStack[hotbar.size() + 1]);

    for (int i = 0; i < 9; i++) {
      damaged.getInventory().setItem(i, array[i]);
    }

    CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");

    super.onEntityDamageByEntity(event);
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}