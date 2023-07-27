package cc.stormworth.hcf.misc.crazyenchants.utils.objects;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.processors.Methods;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CEnchantment {

  private static final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  private String name;
  private String customName;
  private boolean activated;
  private String color;
  private String bookColor;
  private int maxLevel;
  private String infoName;
  private int chance;
  private int chanceIncrease;
  private List<String> infoDescription;
  private final List<Category> categories;
  private EnchantmentType enchantmentType;
  private final CEnchantment instance;

  public CEnchantment(String name) {
    this.instance = this;
    this.name = name;
    this.customName = name;
    this.activated = true;
    this.color = "&7";
    this.bookColor = "&6&l";
    this.maxLevel = 3;
    this.infoName = "&7" + name;
    this.chance = 0;
    this.chanceIncrease = 0;
    this.infoDescription = new ArrayList<>();
    this.categories = new ArrayList<>();
    this.enchantmentType = null;
  }

  public static CEnchantment getCEnchantmentFromName(String enchantment) {
    return enchantmentsManager.getEnchantmentFromName(enchantment);
  }

  public String getName() {
    return name;
  }

  public CEnchantment setName(String name) {
    this.name = name;
    return this;
  }

  public String getCustomName() {
    return customName;
  }

  public CEnchantment setCustomName(String customName) {
    this.customName = customName;
    return this;
  }

  public boolean isActivated() {
    return activated;
  }

  public CEnchantment setActivated(boolean activated) {
    this.activated = activated;
    return this;
  }

  public String getColor() {
    return color;
  }

  public CEnchantment setColor(String color) {
    this.color = CC.translate(color);
    return this;
  }

  public String getBookColor() {
    return bookColor;
  }

  public CEnchantment setBookColor(String bookColor) {
    if (bookColor.startsWith("&f")) {
      bookColor = bookColor.substring(2);
    }
    this.bookColor = CC.translate(bookColor);
    return this;
  }

  public int getMaxLevel() {
    return maxLevel;
  }

  public CEnchantment setMaxLevel(int maxLevel) {
    this.maxLevel = maxLevel;
    return this;
  }

  public String getInfoName() {
    return infoName;
  }

  public CEnchantment setInfoName(String infoName) {
    this.infoName = CC.translate(infoName);
    return this;
  }

  public int getChance() {
    return chance;
  }

  public CEnchantment setChance(int chance) {
    this.chance = chance;
    return this;
  }

  public int getChanceIncrease() {
    return chanceIncrease;
  }

  public CEnchantment setChanceIncrease(int chanceIncrease) {
    this.chanceIncrease = chanceIncrease;
    return this;
  }

  public boolean hasChanceSystem() {
    return chance > 0;
  }

  public boolean chanceSuccesful(int enchantmentLevel) {
    int newChance = chance + (chanceIncrease * (enchantmentLevel - 1));
    int pickedChance = Main.RANDOM.nextInt(100) + 1;
    return newChance >= 100 || newChance <= 0 || pickedChance <= chance;
  }

  public List<String> getInfoDescription() {
    return infoDescription;
  }

  public CEnchantment setInfoDescription(List<String> infoDescription) {
    List<String> info = new ArrayList<>();
    for (String i : infoDescription) {
      info.add(CC.translate(i));
    }
    this.infoDescription = info;
    return this;
  }

  public CEnchantment addCategory(Category category) {
    if (category != null) {
      this.categories.add(category);
    }
    return this;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public CEnchantment setCategories(List<String> categories) {
    for (String categoryString : categories) {
      Category category = enchantmentsManager.getCategory(categoryString);
      if (category != null) {
        this.categories.add(category);
      }
    }
    return this;
  }

  public EnchantmentType getEnchantmentType() {
    return enchantmentType;
  }

  public CEnchantment setEnchantmentType(EnchantmentType enchantmentType) {
    this.enchantmentType = enchantmentType;
    return this;
  }

  public boolean canEnchantItem(ItemStack item) {
    return enchantmentType != null && enchantmentType.canEnchantItem(item);
  }

  public void registerEnchantment() {
    enchantmentsManager.registerEnchantment(instance);
    if (enchantmentType != null) {
      enchantmentType.addEnchantment(instance);
    }
    for (Category category : categories) {
      category.addEnchantment(instance);
    }
  }

  public void unregisterEnchantment() {
    enchantmentsManager.unregisterEnchantment(instance);
    if (enchantmentType != null) {
      enchantmentType.removeEnchantment(instance);
    }
    for (Category category : categories) {
      category.removeEnchantment(instance);
    }
  }

  @Deprecated
  public int getPower(ItemStack item) {
    return getLevel(item);
  }

  public int getLevel(ItemStack item) {
    int level = 0;
    if (Methods.verifyItemLore(item)) {
      for (String lore : item.getItemMeta().getLore()) {
        if (lore.contains(customName)) {
          level = enchantmentsManager.convertLevelInteger(
              lore.replace(color + customName + " ", ""));
          break;
        }
      }
    }
    if (!enchantmentsManager.isUseUnsafeEnchantments() && level > maxLevel) {
      level = maxLevel;
    }
    return level;
  }

}