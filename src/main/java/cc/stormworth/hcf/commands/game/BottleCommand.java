package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.util.player.ExperienceManager;
import java.text.NumberFormat;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class BottleCommand implements Listener {

  @Command(names = {"bottle"}, permission = "WARRIOR")
  public static void bottle(final Player sender) {
    final ItemStack item = sender.getItemInHand();
    if (item == null || item.getType() != Material.GLASS_BOTTLE || item.getAmount() != 1) {
      sender.sendMessage(ChatColor.RED + "You must be holding one glass bottle in your hand.");
      return;
    }
    final ExperienceManager manager = new ExperienceManager(sender);
    final int experience = manager.getCurrentExp();
    manager.setExp(0.0);
    if (experience == 0) {
      sender.sendMessage(ChatColor.RED + "You don't have any experience to bottle!");
      return;
    }
    final ItemStack result = new ItemStack(Material.EXP_BOTTLE);
    final ItemMeta meta = result.getItemMeta();
    meta.setLore(Collections.singletonList(
        ChatColor.GREEN + "XP: " + ChatColor.WHITE + NumberFormat.getInstance()
            .format(experience)));
    result.setItemMeta(meta);
    sender.setItemInHand(result);
    sender.sendMessage(
        ChatColor.GREEN + "You have bottled " + NumberFormat.getInstance().format(experience)
            + " XP!");
    sender.playSound(sender.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
  }


}