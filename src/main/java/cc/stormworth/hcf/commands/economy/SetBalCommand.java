package cc.stormworth.hcf.commands.economy;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SetBalCommand {

  @Command(names = {"SetBal"}, permission = "op")
  public static void setBal(CommandSender sender, @Param(name = "player") UUID player, @Param(name = "amount") float amount) {

    if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
      sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
      return;
    }

    if (Float.isNaN(amount)) {
      sender.sendMessage("§cInvalid amount");
      return;
    }

    Player targetPlayer = Main.getInstance().getServer().getPlayer(player);

    if (targetPlayer == null) {
      sender.sendMessage("§cThat player is not online.");
      return;
    }

    HCFProfile profile = HCFProfile.get(targetPlayer);

    profile.getEconomyData().setBalance(amount);

    if (sender != targetPlayer) {
      sender.sendMessage(CC.translate("&aSet &6" + targetPlayer.getName() + "'s &abalance to &6$" + amount));
    }

    if (sender instanceof Player) {
      String playerDisplayName = ((Player) sender).getDisplayName();
      targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §6" + playerDisplayName);
    } else {
      targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §4CONSOLE§a.");
    }

    //Main.getInstance().getBalanceMap().setBalance(player, (int) amount);
  }

  @Command(names = {"addbal"}, permission = "op")
  public static void addBal(CommandSender sender, @Param(name = "player") UUID player, @Param(name = "amount") float amount) {
    if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
      sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
      return;
    }

    if (Float.isNaN(amount)) {
      sender.sendMessage("§cInvalid amount");
      return;
    }

    HCFProfile profile = HCFProfile.getByUUID(player);


    if (profile == null) {

      CompletableFuture<HCFProfile> future = HCFProfile.load(player);

      future.thenAccept(futureProfile -> {

        if (futureProfile == null) {
          sender.sendMessage(ChatColor.RED + "player not found.");
          return;
        }

        futureProfile.getEconomyData().addBalance(amount);

        if (sender != null) {
          sender.sendMessage(CC.translate("&eAdded &f$" + amount + " &eto &6" + futureProfile.getPlayer().getName() + "'s &ebalance"));
        }

        futureProfile.asyncSave();
      });

      return;
    }

    profile.getEconomyData().addBalance(amount);

    Player targetPlayer = Main.getInstance().getServer().getPlayer(player);

    if (targetPlayer == null) {
      sender.sendMessage("§cThat player is not online.");
      return;
    }

    if (sender != targetPlayer) {
      sender.sendMessage(CC.translate("&aAdded &f$" + amount + " &eto &6" + targetPlayer.getName() + "'s &ebalance"));
    }

    if (sender instanceof Player) {
      String targetDisplayName = ((Player) sender).getDisplayName();
      targetPlayer.sendMessage(CC.translate(targetDisplayName + " &egave you &f$" + amount));
    } else {
      targetPlayer.sendMessage(CC.translate("&4CONSOLE &egave you &f$" + amount));
    }

    //Main.getInstance().getBalanceMap().setBalance(player, (int) EconomyHandler.getBalance(player));
  }

}