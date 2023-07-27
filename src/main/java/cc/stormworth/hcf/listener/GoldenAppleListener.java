package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GoldenAppleListener implements Listener {

  private static final Map<UUID, Long> crappleCooldown = new HashMap<UUID, Long>();

  public static Map<UUID, Long> getCrappleCooldown() {
    return GoldenAppleListener.crappleCooldown;
  }

  @EventHandler
  public void onInteractWithGApple(PlayerInteractEvent event){
    Player player = event.getPlayer();

    if(event.getAction() == Action.LEFT_CLICK_BLOCK){
      if(player.getItemInHand().getType() == Material.GOLDEN_APPLE){

        if (event.getItem().getDurability() == 0){
          if(GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId())){
            long millisRemaining = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();
            double value = millisRemaining / 1000.0;
            double sec = (value > 0.1) ? (FastMath.round(10.0 * value) / 10.0) : 0.1;

            if (GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) > System.currentTimeMillis()) {
              player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            }
          }
        }else{
          if(Main.getInstance().getOppleMap().isOnCooldown(player.getUniqueId())){
            long cooldownUntil = Main.getInstance().getOppleMap().getCooldown(player.getUniqueId());
            if (cooldownUntil > System.currentTimeMillis()) {

              long millisLeft = cooldownUntil - System.currentTimeMillis();
              String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

              event.getPlayer().sendMessage(CC.translate("&cYou are still on cooldown for another §c§l" + msg + "§c."));
            }
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
    final Player player = event.getPlayer();
    if (event.getItem() == null || event.getItem().getType() != Material.GOLDEN_APPLE) {
      return;
    }
    if (EOTWCommand.isFfaEnabled()) {
      if (event.getItem().getDurability() == 0) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Crapples are currently disabled.");
        return;
      } else {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Gapples are currently disabled.");
        return;
      }
    }
    long cooldown = 15000L;
    if (event.getItem().getDurability() == 0 && !GoldenAppleListener.crappleCooldown.containsKey(player.getUniqueId())) {
      GoldenAppleListener.crappleCooldown.put(player.getUniqueId(),
          System.currentTimeMillis() + cooldown);
      return;
    }
    if (event.getItem().getDurability() == 0 && GoldenAppleListener.crappleCooldown.containsKey(player.getUniqueId())) {

      long millisRemaining = GoldenAppleListener.crappleCooldown.get(player.getUniqueId()) - System.currentTimeMillis();
      double value = millisRemaining / 1000.0;
      double sec = (value > 0.1) ? (FastMath.round(10.0 * value) / 10.0) : 0.1;

      if (GoldenAppleListener.crappleCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
        player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
        event.setCancelled(true);
        return;
      }

      GoldenAppleListener.crappleCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
      CooldownManager.addCooldown(player.getUniqueId(), CooldownType.CRAPPLE, (int) TimeUnit.MILLISECONDS.toSeconds(cooldown));
      return;
    }
    if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 0) {
      return;
    }
    if (Main.getInstance().getMapHandler().getGoppleCooldown() == -1) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "Gapples are currently disabled.");
      return;
    }

    long cooldownUntil = Main.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId());
    if (cooldownUntil > System.currentTimeMillis()) {

      long millisLeft = cooldownUntil - System.currentTimeMillis();
      String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "You are still on cooldown for another §c§l" + msg + "§c.");
      return;
    }
    Main.getInstance().getOppleMap().useGoldenApple(event.getPlayer().getUniqueId(),
        Main.getInstance().getMapHandler().isKitMap() ? TimeUnit.MINUTES.toSeconds(15L)
            : TimeUnit.HOURS.toSeconds(2L));

    long millisLeft = Main.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId()) - System.currentTimeMillis();

    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588\u2588"
            + ChatColor.DARK_GREEN + "\u2588\u2588\u2588");
    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588\u2588\u2588" + ChatColor.YELLOW + "\u2588"
            + ChatColor.DARK_GREEN + "\u2588\u2588\u2588\u2588");
    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588"
            + ChatColor.DARK_GREEN + "\u2588\u2588" + ChatColor.GOLD + " Golden Apple:");
    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.WHITE
            + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_GREEN + "\u2588"
            + ChatColor.DARK_GREEN + "   Consumed");
    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.WHITE + "\u2588"
            + ChatColor.GOLD + "\u2588\u2588\u2588\u2588" + ChatColor.DARK_GREEN + "\u2588"
            + ChatColor.YELLOW + " Cooldown Remaining:");
    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588"
            + ChatColor.DARK_GREEN + "\u2588" + ChatColor.BLUE + "   "
            + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588"
            + ChatColor.DARK_GREEN + "\u2588");
    event.getPlayer().sendMessage(
        ChatColor.DARK_GREEN + "\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588"
            + ChatColor.DARK_GREEN + "\u2588\u2588");
  }
}