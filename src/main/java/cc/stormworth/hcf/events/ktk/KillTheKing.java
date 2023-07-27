package cc.stormworth.hcf.events.ktk;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.ktk.commands.KTKCommand;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class KillTheKing {

  private UUID uuid;
  private long started;

  public KillTheKing(final UUID uuid) {
    this.uuid = uuid;
    this.started = System.currentTimeMillis();

    Player player = Bukkit.getServer().getPlayer(uuid);

    if (player != null) {
      for (final Player online : Main.getInstance().getServer().getOnlinePlayers()) {
        online.playSound(online.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
      }

      Bukkit.broadcastMessage(CC.translate("&6[KillTheKing] &r" + player.getDisplayName() + " &ais the king!"));
      player.getActivePotionEffects().clear();
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      player.getInventory().setBoots(
          ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
              .enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_FALL, 6).build());
      player.getInventory().setLeggings(
          ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
              .enchant(Enchantment.DURABILITY, 5).build());
      player.getInventory().setChestplate(ItemBuilder.of(Material.DIAMOND_CHESTPLATE)
          .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5).enchant(Enchantment.DURABILITY, 5)
          .build());
      player.getInventory().setHelmet(
          ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
              .enchant(Enchantment.DURABILITY, 5).build());
      player.getInventory().setItem(0,
          ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 5)
              .enchant(Enchantment.DURABILITY, 5).build());

      player.getInventory().setItem(1, ItemBuilder.of(Material.ENDER_PEARL, 64).build());
      player.getInventory().setItem(6, ItemBuilder.of(Material.STICK).enchant(Enchantment.KNOCKBACK, 4).build());
      for (int i = 2; i < 7; ++i) {

        ItemStack itemStack = player.getInventory().getItem(i);

        if (itemStack == null) {
          ItemStack HP = new ItemStack(Material.POTION, 64, (short) 16421);
          player.getInventory().setItem(i, HP);
        }
      }
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
      player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));

    } else {
      Main.getInstance().setKillTheKing(null);

      if (KTKCommand.killTheKingListener != null) {
        KTKCommand.killTheKingListener.unload();
        KTKCommand.killTheKingListener = null;
      }
    }
  }

  public void win(final Player winner) {
    Team team = Main.getInstance().getTeamHandler().getTeam(winner);

    if (team != null) {
      team.addPoints( 5);
    }
    Bukkit.broadcastMessage(
        CC.translate("&6[KillTheKing] " + winner.getDisplayName() + " &akilled the king!"));
    Main.getInstance().setKillTheKing(null);
    if (KTKCommand.killTheKingListener != null) {
      KTKCommand.killTheKingListener.unload();
      KTKCommand.killTheKingListener = null;
    }
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public void setUuid(final UUID uuid) {
    this.uuid = uuid;
  }

  public long getStarted() {
    return this.started;
  }

  public void setStarted(final long started) {
    this.started = started;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof KillTheKing)) {
      return false;
    }
    final KillTheKing other = (KillTheKing) o;
    if (!other.canEqual(this)) {
      return false;
    }
    final Object this$uuid = this.getUuid();
    final Object other$uuid = other.getUuid();
    if (this$uuid == null) {
      if (other$uuid == null) {
        return this.getStarted() == other.getStarted();
      }
    } else if (this$uuid.equals(other$uuid)) {
      return this.getStarted() == other.getStarted();
    }
    return false;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof KillTheKing;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $uuid = this.getUuid();
    result = result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
    final long $started = this.getStarted();
    result = result * 59 + (int) ($started >>> 32 ^ $started);
    return result;
  }

  @Override
  public String toString() {
    return "KillTheKing(uuid=" + this.getUuid() + ", started=" + this.getStarted() + ")";
  }
}
