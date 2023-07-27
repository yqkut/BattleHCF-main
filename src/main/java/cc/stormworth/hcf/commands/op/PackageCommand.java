package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.misc.partnerpackages.EditPackageListener;
import cc.stormworth.hcf.misc.partnerpackages.PackagesMenu;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @Author NulledCode
 * @Plugin BattleHCF
 * @Date 2022-04
 */
public class PackageCommand {

  @Command(names = {"partnerpackages", "partnerpackage"}, permission = "")
  public static void partnerpackages(final Player player) {
    new PackagesMenu().openMenu(player);
  }

  @Command(names = {"partnerpackages edit", "partnerpackage edit"}, permission = "op")
  public static void partnerpackagesEdit(final Player player) {
    Inventory inventory = Bukkit.createInventory(null, 54, "Editing PPItems");
    ItemStack[] contents = EditPackageListener.PPItems;
    inventory.setContents(contents);
    player.openInventory(inventory);
  }

  @Command(names = {"partnerpackages rewards", "partnerpackage rewards"}, permission = "op")
  public static void partnerpackagesvRewards(final Player player) {
    Inventory inventory = Bukkit.createInventory(null, 54, "Editing PPRewards");
    ItemStack[] contents = EditPackageListener.PPRewards;
    inventory.setContents(contents);
    player.openInventory(inventory);
  }

  @Command(names = {"package give"}, permission = "op")
  public static void packagegive(CommandSender sender,
      @Param(name = "player", defaultValue = "self") Player target,
      @Param(name = "amount") int amount) {
    target.getInventory().addItem(getPartnerPackage(amount));
    sender.sendMessage(CC.translate("&f You have given " + target.getDisplayName() + " " + amount
        + " &6&lPartner Packages&f!"));
    target.sendMessage(
        CC.translate("&f You have been given " + amount + " &6&lPartner Packages&f!"));
  }


  @Command(names = {"package all"}, permission = "op")
  public static void packageall(CommandSender sender, @Param(name = "amount") int amount) {
    sender.sendMessage(CC.translate("&aYou have given all players &aa &d&lPartner Package&a."));
    for (Player online : Bukkit.getServer().getOnlinePlayers()) {
      online.getInventory().addItem(getPartnerPackage(amount));
    }
  }

  public static ItemStack getPartnerPackage(int amount) {
    return new ItemBuilder(Material.GOLD_NUGGET).amount(amount)
        .name(CC.translate("&6&lAbility Gift")).setLore(CC.translate(
            Arrays.asList("", "&&eFind &6common &eabilities and", "maybe exclusive ones.", "",
                "&6&nstore.battle.rip"))).build();
  }
}
