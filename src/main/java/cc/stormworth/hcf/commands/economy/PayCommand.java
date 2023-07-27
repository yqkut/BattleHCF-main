package cc.stormworth.hcf.commands.economy;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.economy.EconomyData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.UUID;

public class PayCommand {

  @Command(names = {"Pay", "P2P"}, permission = "")
  public static void pay(Player sender, @Param(name = "player") UUID player, @Param(name = "amount") float amount) {

    HCFProfile profile = HCFProfile.get(sender);

    if (profile.isDeathBanned()) {
      sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
      return;
    }

    EconomyData economyData = profile.getEconomyData();

    double balance = economyData.getBalance();
    Player bukkitPlayer = Main.getInstance().getServer().getPlayer(player);

    if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
      sender.sendMessage(ChatColor.RED + "That player is not online.");
      return;
    }

    HCFProfile targetProfile = HCFProfile.get(bukkitPlayer);
    EconomyData targetEconomyData = targetProfile.getEconomyData();

    if (targetProfile.isDeathBanned()) {
      sender.sendMessage(ChatColor.RED + "You can't do this because that player is deathbanned.");
      return;
    }

    if (sender.equals(bukkitPlayer)) {
      sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
      return;
    }

    if (amount < 5) {
      sender.sendMessage(ChatColor.RED + "You must send at least $5!");
      return;
    }

    if (Double.isNaN(balance)) {
      sender.sendMessage("§cYou can't send money because your balance is fucked.");
      return;
    }

    if (Float.isNaN(amount)) {
      sender.sendMessage(ChatColor.RED + "Nope.");
      return;
    }

    if (balance < amount) {
      sender.sendMessage(ChatColor.RED + "You do not have $" + amount + "!");
      return;
    }

    if (!targetProfile.isPaymentsToggled()) {
      sender.sendMessage(ChatColor.RED + "That player has payments disabled.");
      return;
    }

    economyData.subtractBalance(amount);
    targetEconomyData.addBalance(amount);

    sender.sendMessage(
        ChatColor.YELLOW + "You sent " + ChatColor.GOLD + NumberFormat.getCurrencyInstance().format(amount) +
                ChatColor.YELLOW + " to " + ChatColor.GOLD + UUIDUtils.name(player) + ChatColor.YELLOW + ".");

    bukkitPlayer.sendMessage(
        ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " sent you "
            + ChatColor.GOLD + NumberFormat.getCurrencyInstance().format(amount)
            + ChatColor.YELLOW + ".");
  }

}