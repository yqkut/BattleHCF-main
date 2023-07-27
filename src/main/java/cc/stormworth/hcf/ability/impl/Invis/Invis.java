package cc.stormworth.hcf.ability.impl.Invis;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.RestoreEffect;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Invis extends InteractAbility {

  @Getter
  private final Set<UUID> players = new HashSet<>();
  private final Set<UUID> offline = new HashSet<>();
  private final int duration = 180 * 20;

  public Invis() {
    super("Camouflage", "&aCamouflage",
        Lists.newArrayList(
            "",
            "&7Become a ghost for you enemies, get invisibility",
            "&7for 3 minutes to go unnoticed",
            ""
        ),
        ItemBuilder.of(Material.INK_SACK).build(), TimeUtil.parseTimeLong("3m"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (!Main.getInstance().getMapHandler().isKitMap() && Main.getInstance().getServerHandler()
        .isWarzone(player.getLocation())) {
      player.sendMessage(CC.RED + "You cannot use this item within warzone.");
      return;
    }
    this.hidePlayer(player, this.duration);
    event.setCancelled(true);
    super.onInteract(event);
  }

  public void hidePlayer(Player player, int duration) {
    PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1);
    player.addPotionEffect(effect, true);

    updateArmor(player, true);

    this.players.add(player.getUniqueId());
  }

  public void hidePlayers(Player player) {
    for (UUID uuid : this.players) {
      Player online = Bukkit.getPlayer(uuid);

      if (!player.getWorld().getPlayers().contains(online)) continue;

      if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {

        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {

          if (activePotionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
            Main.getInstance().getEffectRestorer().setRestoreEffect(online, new PotionEffect(PotionEffectType.INVISIBILITY, 0, Integer.MAX_VALUE));
          }
        }
      }

      updateArmorFor(player, online, true);
    }
  }

  private void showPlayer(Player player, boolean forced) {
    this.players.remove(player.getUniqueId());

    if (forced) {
      RestoreEffect effect = Main.getInstance().getEffectRestorer().getRestores().remove(player.getUniqueId(), Effect.getByPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 0, Integer.MAX_VALUE)));

      if (effect == null) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
      } else {
        player.addPotionEffect(effect.getEffect(), true);
      }
    }

    updateArmor(player, false);
  }

  public void updateArmor(Player player, boolean remove) {
    Set<PacketPlayOutEntityEquipment> packets = this.getEquipmentPackets(player, remove);

    for (Player other : player.getWorld().getPlayers()) {
      if (other == player) {
        continue;
      }

      for (PacketPlayOutEntityEquipment packet : packets) {
        sendPacket(other, packet);
      }
    }

    player.updateInventory();
  }

  public void updateArmorFor(Player player, Player target, boolean remove) {
    Set<PacketPlayOutEntityEquipment> packets = this.getEquipmentPackets(target, remove);

    for (PacketPlayOutEntityEquipment packet : packets) {
      sendPacket(player, packet);
    }
  }

  public void sendPacket(Player player, Object packet) {
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet) packet);
  }

  private Set<PacketPlayOutEntityEquipment> getEquipmentPackets(Player player, boolean remove) {
    Set<PacketPlayOutEntityEquipment> packets = new HashSet<>();
    for (int slot = 1; slot < 5; slot++) {
      PacketPlayOutEntityEquipment equipment = InvisPacketHelper.createEquipmentPacket(player, slot,
          remove);
      packets.add(equipment);
    }
    return packets;
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getDamager() instanceof Player)) {
      return;
    }

    Player target = (Player) event.getEntity();

    if (this.players.contains(target.getUniqueId())) {

      if (Main.getInstance().getTeamHandler().getTeam(target.getUniqueId()) != null
              && Main.getInstance().getTeamHandler().getTeam(target.getUniqueId()).isMember(event.getDamager().getUniqueId())) {
        return;
      }

      this.showPlayer(target, true);
      target.sendMessage(CC.RED + "You have become visible because received damage.");
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    if (this.players.contains(player.getUniqueId())) {
      this.showPlayer(player, true);
      this.offline.add(player.getUniqueId());
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (this.offline.remove(player.getUniqueId())) {
      this.hidePlayer(player, this.duration);
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (this.players.contains(player.getUniqueId())) {
      this.showPlayer(player, true);
    }
  }

  @EventHandler
  public void onPotionEffectExpire(PotionEffectExpireEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (this.players.contains(player.getUniqueId())) {
      this.showPlayer(player, false);
    }
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return null;
  }
}