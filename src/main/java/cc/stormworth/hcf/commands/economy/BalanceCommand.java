package cc.stormworth.hcf.commands.economy;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BalanceCommand {

  @Command(names = {"Balance", "Econ", "Bal"}, permission = "")
  public static void balance(Player sender, @Param(name = "player", defaultValue = "self") UUID player) {

    HCFProfile profile = HCFProfile.getByUUID(player);

    if (profile == null) {

      CompletableFuture<HCFProfile> future = HCFProfile.load(player);

        future.thenAccept(profileFuture -> {

            if (profileFuture == null) {
              sender.sendMessage(ChatColor.RED + "Player not found.");
              return;
            }

          sender.sendMessage(CC.translate("&6Balance of &l" + profileFuture.getName() + ": &f" + profileFuture.getEconomyData().getFormattedBalance()));
        });

      return;
    }

    if (sender.getUniqueId().equals(player)) {
      sender.sendMessage(CC.translate("&6Balance: &f" + profile.getEconomyData().getFormattedBalance()));
    } else {
      sender.sendMessage(CC.translate("&6Balance of &l" + profile.getName() + ": &f" + profile.getEconomyData().getFormattedBalance()));
    }
  }

}