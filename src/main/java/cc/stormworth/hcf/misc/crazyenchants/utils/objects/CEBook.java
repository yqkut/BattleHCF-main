package cc.stormworth.hcf.misc.crazyenchants.utils.objects;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.inventory.ItemStack;

public class CEBook {

  private CEnchantment enchantment;
  private int amount;
  private int level;
  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  public CEBook(CEnchantment enchantment) {
    this(enchantment, 1, 1);
  }

  public CEBook(CEnchantment enchantment, int level) {
    this(enchantment, level, 1);
  }

  public CEBook(CEnchantment enchantment, int level, int amount) {
    this.enchantment = enchantment;
    this.amount = amount;
    this.level = level;
  }

  public CEBook(CEnchantment enchantment, int level, Category category) {
    this(enchantment, level, 1, category);
  }

  public CEBook(CEnchantment enchantment, int level, int amount, Category category) {
    this.enchantment = enchantment;
    this.amount = amount;
    this.level = level;
  }

  public CEBook(CEnchantment enchantment, int level, int amount, int destroyRate, int successRate) {
    this.enchantment = enchantment;
    this.amount = amount;
    this.level = level;
  }

  public CEnchantment getEnchantment() {
    return this.enchantment;
  }

  public CEBook setEnchantment(CEnchantment enchantment) {
    this.enchantment = enchantment;
    return this;
  }

  public int getAmount() {
    return this.amount;
  }

  public CEBook setAmount(int amount) {
    this.amount = amount;
    return this;
  }

  public int getLevel() {
    return this.level;
  }

  public CEBook setLevel(int level) {
    this.level = level;
    return this;
  }

  public ItemBuilder getItemBuilder() {
    String name = enchantment.getBookColor() + enchantment.getCustomName() + " "
        + enchantmentsManager.convertLevelString(level);
    List<String> lore = new ArrayList<>();
    for (String bookLine : Files.CUSTOMENCHANTS.getFile()
        .getStringList("Settings.EnchantmentBookLore")) {
      if (bookLine.contains("%Description%") || bookLine.contains("%description%")) {
        for (String enchantmentLine : enchantment.getInfoDescription()) {
          lore.add(CC.translate(enchantmentLine));
        }
      } else {
        lore.add(CC.translate(bookLine)
            .replace("%Destroy_Rate%", "0").replace("%destroy_rate%", "0")
            .replace("%Success_Rate%", "100").replace("%success_rate%", "100"));
      }
    }
    return enchantmentsManager.getEnchantmentBook().amount(amount).name(name).setLore(lore);
  }

  public ItemStack buildBook() {
    return getItemBuilder().build();
  }

  private int percentPick(int max, int min) {
    return max == min ? max : min + new Random().nextInt(max - min);
  }
}