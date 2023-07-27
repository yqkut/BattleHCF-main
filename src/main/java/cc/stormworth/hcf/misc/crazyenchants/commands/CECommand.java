package cc.stormworth.hcf.misc.crazyenchants.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.controllers.ShopControl;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import cc.stormworth.hcf.misc.crazyenchants.utils.managers.InfoMenuManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEBook;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.Category;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.EnchantmentType;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CECommand {

  private static final EnchantmentsManager ce = Main.getInstance().getEnchantmentsManager();

  @Command(names = {"ce"}, permission = "", requiresPlayer = true)
  public static void ce(Player player) {
    ShopControl.openGUI(player);
  }

  @Command(names = {"ce reload"}, permission = "op")
  public static void reload(CommandSender sender) {
    ce.getFileManager().setup(Main.getInstance());
    ce.reload();
    sender.sendMessage(CC.translate(CC.PRIMARY + "&lBattle&7 &7┃ &aConfig Reloaded."));
  }

  @Command(names = {"ce debug"}, permission = "op")
  public static void debug(CommandSender sender) {
    List<String> brokenEnchantments = new ArrayList<>();
    List<String> brokenEnchantmentTypes = new ArrayList<>();
    for (CEnchantments enchantment : CEnchantments.values()) {
      if (!FileManager.Files.CUSTOMENCHANTS.getFile()
          .contains("Enchantments." + enchantment.getName())) {
        brokenEnchantments.add(enchantment.getName());
      }
      if (enchantment.getType() == null) {
        brokenEnchantmentTypes.add(enchantment.getName());
      }
    }
    if (brokenEnchantments.isEmpty() && brokenEnchantmentTypes.isEmpty()) {
      sender.sendMessage(
          CC.translate(CC.PRIMARY + "&lBattle&7 &7┃ &aAll enchantments are loaded."));
    } else {
      if (!brokenEnchantments.isEmpty()) {
        int i = 1;
        sender.sendMessage(CC.translate(CC.PRIMARY + "&lBattle&7 &7┃ &cMissing Enchantments:"));
        sender.sendMessage(CC.translate(CC.PRIMARY
            + "&lBattle&7 &7┃ &7These enchantments are broken due to one of the following reasons:"));
        for (String broke : brokenEnchantments) {
          sender.sendMessage(CC.translate("&c#" + i + ": &6" + broke));
          i++;
        }
        sender.sendMessage(CC.translate("&7- &cMissing from the Enchantments.yml"));
        sender.sendMessage(CC.translate("&7- &c<Enchantment Name>: option was changed"));
        sender.sendMessage(CC.translate("&7- &cYaml format has been broken."));
      }
      if (!brokenEnchantmentTypes.isEmpty()) {
        int i = 1;
        sender.sendMessage(
            CC.translate(CC.PRIMARY + "&lBattle&7 &7┃ &cEnchantments with null types:"));
        sender.sendMessage(CC.translate(CC.PRIMARY
            + "&lBattle&7 &7┃ &7These enchantments are broken due to the enchantment type being null."));
        for (String broke : brokenEnchantmentTypes) {
          sender.sendMessage(CC.translate("&c#" + i + ": &6" + broke));
          i++;
        }
      }
    }
    sender.sendMessage(CC.translate(
        CC.PRIMARY + "&lBattle&7 &7┃ &cEnchantment Types and amount of items in each:"));
    for (EnchantmentType enchantmentType : InfoMenuManager.getInstance().getEnchantmentTypes()) {
      sender.sendMessage(CC.translate(
          "&c" + enchantmentType.getName() + ": &6" + enchantmentType.getEnchantableMaterials()
              .size()));
    }
  }

  @Command(names = {"ce info"}, permission = "op", requiresPlayer = true)
  public static void info(CommandSender sender) {
    ce.getInfoMenuManager().openInfoMenu((Player) sender);
  }

  @Command(names = {"ce spawn"}, permission = "op", requiresPlayer = true)
  public static void spawn(Player player, @Param(name = "enchantment") CEnchantment enchantment,
      @Param(name = "level", defaultValue = "1") int level) {
    Category category = ce.getCategory(enchantment.getName());
    Location location = player.getLocation();
    if (enchantment == null && category == null) {
      player.sendMessage(CC.translate(CC.PRIMARY + "&lBattle&7 &7┃ &cInvalid enchantment."));
      return;
    }
    location.getWorld().dropItemNaturally(location,
        category == null ? new CEBook(enchantment, level).buildBook()
            : category.getLostBook().getLostBook(category));
    player.sendMessage(CC.translate(
        "&eYou have spawned a book at &6" + location.getBlockX() + "&7, &6" + location.getBlockX()
            + "&7, &6" + location.getBlockX() + " &7(" + location.getWorld().getName() + ")&e."));
  }

  @Command(names = {"ce book"}, permission = "op")
  public static void book(CommandSender sender,
      @Param(name = "target", defaultValue = "self") Player target,
      @Param(name = "enchantment") CEnchantment enchantment, @Param(name = "level") int level,
      @Param(name = "amount") int amount) {
    if (enchantment == null) {
      sender.sendMessage(CC.translate(CC.PRIMARY + "&lBattle&7 &7┃ &cInvalid enchantment."));
      return;
    }
    if (sender instanceof Player) {
      sender.sendMessage(CC.translate(
          CC.PRIMARY + "&lBattle&7 &7┃ &eAdded &6x" + amount + " " + enchantment.getName()
              + " &elevel &6" + level + " &ebook to &6" + target.getName() + "&e."));
    }
    target.getInventory().addItem(new CEBook(enchantment, level, amount).buildBook());
  }
}