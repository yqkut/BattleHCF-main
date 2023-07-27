package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Hit;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class HelmetDisarmer extends DamageableAbility {

  private final static String USES_LORE = CC.GRAY + "Uses:" + CC.WHITE + " %s";

  public HelmetDisarmer() {
    super("HelmetDisarmer",
        "&eHelmetDisarmer",
        Lists.newArrayList(
                "",
                "&7Having trouble? Remove your enemies helmet with this ability.",
                "",
                String.format(USES_LORE, 3),
                ""
        ),
        new ItemBuilder(Material.GOLD_AXE).setUnbreakable(true).build(),
        TimeUtil.parseTimeLong("1m40s"));
  }

  @Override
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    Player damager = (Player) event.getDamager();
    Player damaged = (Player) event.getEntity();

    if (!hasDiamondArmor(damaged)) {
      damager.sendMessage(CC.translate("&cThis ability can only be used on Diamonds."));
      return;
    }

    HCFProfile profileDamager = HCFProfile.get(damager);

    Hit hit = profileDamager.getHit();

    if (hit == null || hit.getUuid() != damaged.getUniqueId() || !isItem(hit.getItemStack())) {
      hit = new Hit(damaged.getUniqueId(), getItem());
      profileDamager.setHit(hit);
    }

    hit.setHits(hit.getHits() + 1);

    if ((3 - hit.getHits()) > 0) {
      damager.sendMessage(
          CC.translate("&6&l[&e✷&6&l] &eYou have to hit your enemy &e&l" + (3 - hit.getHits())
              + " more times."));
    }

    if (hit.getHits() < 3) {
      return;
    }

    hit.setHits(0);

    HCFProfile profile = HCFProfile.get(damaged);

    /*if (damaged.getInventory().firstEmpty() == -1) {
      if (damaged.getInventory().first(Material.POTION) == -1) {
        profile.setHelmet(damaged.getInventory().getHelmet());

        TaskUtil.runLater(Main.getInstance(), () -> {
          ItemStack helmet = profile.getHelmet();
          if (helmet != null) {
            damaged.getInventory().setHelmet(helmet);
            profile.setHelmet(null);
            damaged.updateInventory();
          }
        }, 20 * 6);

      } else {
        damaged.getInventory().setItem(damaged.getInventory().first(Material.POTION), damaged.getInventory().getHelmet());
      }
    } else {

      boolean removed = false;

      *//*for (int i = 9; i < damaged.getInventory().getSize(); i++) {
        ItemStack item = damaged.getInventory().getItem(i);

        if(item == null || item.getType() == Material.AIR) {
          removed = true;
          damaged.getInventory().setItem(i, damaged.getInventory().getHelmet());
          break;
        }
      }*//*


    }*/

    profile.setHelmet(damaged.getInventory().getHelmet());

    TaskUtil.runLater(Main.getInstance(), () -> {
      ItemStack helmet = profile.getHelmet();
      if (helmet != null) {
        damaged.getInventory().setHelmet(helmet);
        profile.setHelmet(null);
        damaged.updateInventory();
      }
    }, 20 * 6);

    damaged.sendMessage(CC.translate("&6&lWatch out!&e you have hit by &c&lHelmet Disarmer"));

    damaged.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));

    CooldownAPI.setCooldown(damaged, "InvCooldown", TimeUtil.parseTimeLong("6s"));

    CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
    super.onEntityDamageByEntity(event);
  }

  @Override
  public void consume(Player player) {
    ItemStack abilityItem = null;

    for (ItemStack item : player.getInventory().getContents()) {
      if (isItem(item)) {
        abilityItem = item;
        break;
      }
    }

    if (abilityItem == null) {
      return;
    }

    List<String> lore = abilityItem.getItemMeta().getLore();

    if (lore == null) {
      return;
    }

    int uses = Integer.parseInt(ChatColor.stripColor(lore.get(3).split(" ")[1]));

    if (uses <= 1) {
      player.getInventory().remove(abilityItem);
      player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
      return;
    }

    lore.set(3, String.format(USES_LORE, uses - 1));

    ItemMeta meta = player.getItemInHand().getItemMeta();
    meta.setLore(lore);

    meta.setLore(lore);

    abilityItem.setItemMeta(meta);

    player.getInventory().setItem(player.getInventory().first(abilityItem), abilityItem);
  }

  @Override
  public boolean isItem(ItemStack item) {
    return item != null &&
            item.getType() == getItemOriginal().getType() &&
            item.hasItemMeta() &&
            item.getItemMeta().hasDisplayName() &&
            item.getItemMeta().getDisplayName().equalsIgnoreCase(getItemOriginal().getItemMeta().getDisplayName()) &&
            (item.getItemMeta().hasLore());
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {

    if (event.getSlot() == 39) {
      Player player = (Player) event.getWhoClicked();
      if (CooldownAPI.hasCooldown(player, "InvCooldown")) {
        player.sendMessage(CC.translate("&cYou put helmet on cooldown."));
        event.setCancelled(true);
      }
    }
  }

  private boolean hasDiamondArmor(Player player) {
    return player.getInventory().getHelmet() != null
        && player.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET
        && player.getInventory().getChestplate() != null
        && player.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE
        && player.getInventory().getLeggings() != null
        && player.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS
        && player.getInventory().getBoots() != null
        && player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS;
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}