package cc.stormworth.hcf.commands.gems;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GemsCommand {

  @Command(names = {"gems", "gem"}, permission = "DEFAULT")
  public static void gems(Player player) {
    HCFProfile profile = HCFProfile.get(player);

    player.sendMessage(CC.translate("&aYou have &f" + profile.getGems() + " &agems."));
  }

  @Command(names = {"gems all"}, permission = "op")
  public static void gemsAll(Player player, @Param(name = "amount") int amount) {

    for (Player other : Bukkit.getOnlinePlayers()){
        HCFProfile profile = HCFProfile.get(other);
        profile.setGems(profile.getGems() + amount);
        other.sendMessage(CC.translate("&eYou received &6" + amount + " &egems."));

      TitleBuilder titleBuilder = new TitleBuilder("&a&lGems All &areceived.", "&eAmount: &f" + amount, 10, 20, 10);
      titleBuilder.send(other);
    }

    player.sendMessage(CC.translate("&aYou have given &f" + amount + " &agems to everyone."));
  }

  @Command(names = {"gems help", "gem help"}, permission = "DEVELOPER")
  public static void help(CommandSender player) {

    player.sendMessage(CC.translate("&a&lGems &aHelp: "));
    player.sendMessage(
        CC.translate("&a/gems pay <player> <amount> &7- &fto pay gems to a player."));

    if (player.hasPermission("gems.admin")) {
      player.sendMessage(CC.translate("&a/gems give <player> <amount> &7- &fGive a player gems."));
      player.sendMessage(CC.translate("&a/gems set <player> <amount> &7- &fSet a player's gems."));
      player.sendMessage(
          CC.translate("&a/gems take <player> <amount> &7- &fTake a player's gems."));
      player.sendMessage(CC.translate("&a/gems reset <player> &7- &fReset a player's gems."));
    }
  }

  @Command(names = {"gems pay", "gem pay"}, permission = "")
  public static void pay(Player player, @Param(name = "player") Player target,
      @Param(name = "amount") int amount) {
    HCFProfile profile = HCFProfile.get(player);
    HCFProfile profileTarget = HCFProfile.get(target);

    if (profile.getGems() < amount) {
      player.sendMessage(CC.translate("&cYou don't have enough gems."));
      return;
    }

    if (!profileTarget.isPaymentsToggled()) {
      player.sendMessage(ChatColor.RED + "That player has payments disabled.");
      return;
    }

    profile.removeGems(amount);
    profileTarget.addGems(amount);
    player.sendMessage(
        CC.translate("&aYou have paid &f" + amount + " &agems to &f" + target.getName() + "."));
    target.sendMessage(CC.translate(
        "&aYou have received &f" + amount + " &agems from &f" + player.getName() + "."));

  }


  @Command(names = {"gems give", "gem give"}, permission = "DEVELOPER")
  public static void gemsGive(CommandSender player, @Param(name = "player") Player target,
      @Param(name = "amount") int amount) {

    HCFProfile profileTarget = HCFProfile.get(target);

    if (player.hasPermission("gems.admin")) {
      profileTarget.addGems(amount);
      player.sendMessage(CC.translate("&aYou have given &f" + target.getName() + " &agems."));
      target.sendMessage(CC.translate("&aYou have received &f" + amount + " &agems."));
      return;
    }

    profileTarget.addGems(amount);

    player.sendMessage(CC.translate("&aYou have given &f" + target.getName() + " &agems."));
    target.sendMessage(CC.translate(
        "&aYou have received &f" + amount + " &agems from &f" + player.getName() + "."));
  }

  @Command(names = {"gems set", "gem set"}, permission = "DEVELOPER")
  public static void gemsSet(Player player, @Param(name = "player") Player target,
      @Param(name = "amount") int amount) {

    HCFProfile profileTarget = HCFProfile.get(target);

    profileTarget.setGems(amount);

    player.sendMessage(CC.translate("&aYou have set &f" + target.getName() + " &agems."));
    target.sendMessage(CC.translate("&aYou have received &f" + amount + " &agems."));
  }

  @Command(names = {"gems take", "gems delete", "gems remove", "gem take", "gem delete",
      "gem remove"}, permission = "DEVELOPER")
  public static void gemsTake(Player player, @Param(name = "player") Player target,
      @Param(name = "amount") int amount) {

    HCFProfile profileTarget = HCFProfile.get(target);

    profileTarget.removeGems(amount);

    player.sendMessage(CC.translate("&aYou have taken &f" + target.getName() + " &agems."));
    target.sendMessage(CC.translate("&aYou have taken &f" + amount + " &agems."));
  }

  @Command(names = {"gems reset", "gem reset"}, permission = "DEVELOPER")
  public static void gemsReset(Player player, @Param(name = "player") Player target) {

    HCFProfile profileTarget = HCFProfile.get(target);

    profileTarget.setGems(0);

    player.sendMessage(CC.translate("&aYou have reset &f" + target.getName() + " &agems."));
    target.sendMessage(CC.translate("&aYou have reset your gems."));
  }

}