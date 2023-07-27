package cc.stormworth.hcf.misc.lunarclient.cooldown;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.cooldowntimer.Timer;
import cc.stormworth.hcf.util.cooldowntimer.events.TimerActivateEvent;
import cc.stormworth.hcf.util.cooldowntimer.events.TimerCancelEvent;
import cc.stormworth.hcf.util.cooldowntimer.events.TimerExpireEvent;
import cc.stormworth.hcf.util.cooldowntimer.impl.PlayerTimer;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager implements Listener {

  @Getter
  private static Map<CooldownType, LunarClientCooldown> cooldowns;

  public CooldownManager() {
    cooldowns = new HashMap<>();
    this.setupCooldowns();
    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }

  public static void addCooldown(UUID uuid, CooldownType type, int duration) {
      if (!Main.getInstance().getLunarClientManager().getPlayers().contains(uuid)) {
          return;
      }
      if (!cooldowns.containsKey(type)) {
          return;
      }
      sendCooldown(uuid, cooldowns.get(type).createCooldown(duration));
  }

  public static void removeCooldown(UUID uuid, CooldownType type) {
      if (!Main.getInstance().getLunarClientManager().getPlayers().contains(uuid)) {
          return;
      }
      if (!cooldowns.containsKey(type)) {
          return;
      }
      if (Bukkit.getPlayer(uuid) != null) {
          LunarClientAPICooldown.clearCooldown(Bukkit.getPlayer(uuid),
              cooldowns.get(type).getName());
      }
  }

  public static void sendCooldown(UUID uuid, LCCooldown cooldown) {
    Player player = Bukkit.getPlayer(uuid);
      if (player == null) {
          return;
      }
    LunarClientAPICooldown.sendCooldown(player, cooldown.getName());
  }

  public static void removeCooldown(UUID uuid, LCCooldown cooldown) {
    Player player = Bukkit.getPlayer(uuid);
      if (player == null) {
          return;
      }
    LunarClientAPICooldown.clearCooldown(player, cooldown.getName());
  }

  public void disable() {
    cooldowns.clear();
  }

  private void setupCooldowns() {
    LunarClientCooldown enderPearl = new LunarClientCooldown("EnderPearl", Material.ENDER_PEARL);
    LunarClientCooldown combatTag = new LunarClientCooldown("CombatTag", Material.DIAMOND_SWORD);
    LunarClientCooldown crapple = new LunarClientCooldown("Crapple", Material.GOLDEN_APPLE);
    LunarClientCooldown home = new LunarClientCooldown("HQ", Material.BED);
    LunarClientCooldown logout = new LunarClientCooldown("Logout", Material.RED_ROSE);
    LunarClientCooldown stuck = new LunarClientCooldown("Stuck", Material.FENCE_GATE);
    LunarClientCooldown camp = new LunarClientCooldown("Camp", Material.LEASH);

    cooldowns.put(CooldownType.ENDERPEARL, enderPearl);
    cooldowns.put(CooldownType.COMBAT_TAG, combatTag);
    cooldowns.put(CooldownType.CRAPPLE, crapple);
    cooldowns.put(CooldownType.HOME, home);
    cooldowns.put(CooldownType.LOGOUT, logout);
    cooldowns.put(CooldownType.STUCK, stuck);
    cooldowns.put(CooldownType.CAMP, camp);

    LunarClientAPICooldown.registerCooldown(enderPearl.createCooldown(16));
    LunarClientAPICooldown.registerCooldown(combatTag.createCooldown(45));
    LunarClientAPICooldown.registerCooldown(home.createCooldown(10));
    LunarClientAPICooldown.registerCooldown(crapple.createCooldown(15));
    LunarClientAPICooldown.registerCooldown(home.createCooldown(10));
    LunarClientAPICooldown.registerCooldown(logout.createCooldown(45));
    LunarClientAPICooldown.registerCooldown(stuck.createCooldown(60));
    LunarClientAPICooldown.registerCooldown(camp.createCooldown(20));
  }

  @EventHandler(ignoreCancelled = true)
  public void onTimerActivate(TimerActivateEvent event) {
    CooldownType type = this.getCooldown(event.getTimer());
      if (type == null) {
          return;
      }

    addCooldown(event.getUuid(), type, event.getDelay());
  }

  @EventHandler
  public void onTimerCancel(TimerCancelEvent event) {
    CooldownType type = this.getCooldown(event.getTimer());
      if (type == null) {
          return;
      }

    removeCooldown(event.getUuid(), type);
  }

  @EventHandler
  public void onTimerExpire(TimerExpireEvent event) {
    CooldownType type = this.getCooldown(event.getTimer());
      if (type == null) {
          return;
      }

    removeCooldown(event.getUuid(), type);
  }

  private CooldownType getCooldown(Timer timer) {
    return !(timer instanceof PlayerTimer) ? null : timer.getLunarCooldownType();
  }
}