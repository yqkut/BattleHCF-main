package cc.stormworth.hcf.misc.crazyenchants.utils.enums;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.EnchantmentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum CEnchantments {

  //	----------------Swords----------------  \\
  NUTRITION("Nutrition", "Sword", 15, 5),
  //HEADLESS("Headless", "Sword", 10, 10),
  //	----------------Helmets----------------  \\
  TURTLESHELL("TurtleShell", "Helmet"),
  INFRARED("Infrared", "Helmet"),
  MERMAID("Mermaid", "Helmet"),
  IMPLANTS("Implants", "Helmet", 5, 5),
  //	----------------Armor----------------  \\
  RECOVER("Recover", "Armor"),
  SPECTRAL("Spectral", "Armor"),
  FIRERESISTANCE("FireResistance", "Armor"),
  //	----------------Boots----------------  \\
  SPEED("Speed", "Boots"),
  //	----------------Axes----------------  \\
  GLUTTONY("Gluttony", "Axe", 10, 5),
  DECAPITATION("Decapitation", "Attack-Items", 10, 10),
  //	----------------PickAxes----------------  \\
  EXPERIENCE("Experience", "Pickaxe", 25, 25),
  //	----------------Tools----------------  \\
  OXYGENATE("Oxygenate", "Tool"),
  //	----------------All----------------  \\
  HELLFORGED("HellForged", "Damaged-Items", 5, 5);

  private String name;
  private String typeName;
  private boolean hasChanceSystem;
  private int chance;
  private int chanceIncrease;

  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  private CEnchantment cachedEnchantment = null;

  CEnchantments(String name, String typeName) {
    this.name = name;
    this.typeName = typeName;
    this.chance = 0;
    this.chanceIncrease = 0;
    this.hasChanceSystem = false;
  }

  CEnchantments(String name, String typeName, int chance, int chanceIncrease) {
    this.name = name;
    this.typeName = typeName;
    this.chance = chance;
    this.chanceIncrease = chanceIncrease;
    this.hasChanceSystem = true;
  }

  public static void invalidateCachedEnchants() {
    for (CEnchantments value : values()) {
      value.cachedEnchantment = null;
    }
  }

  public static CEnchantments getFromName(String enchant) {
    for (CEnchantments ench : values()) {
      if (ench.getName().equalsIgnoreCase(enchant) || ench.getCustomName()
          .equalsIgnoreCase(enchant)) {
        return ench;
      }
    }
    return null;
  }

  public static List<CEnchantments> getFromeNames(List<CEnchantment> enchantments) {
    List<CEnchantments> cEnchantments = new ArrayList<>();
    for (CEnchantment cEnchantment : enchantments) {
      CEnchantments enchantment = getFromName(cEnchantment.getName());
      if (enchantment != null) {
        cEnchantments.add(enchantment);
      }
    }
    return cEnchantments;
  }

  public String getName() {
    return name;
  }

  public String getCustomName() {
    return getEnchantment().getCustomName();
  }

  public int getChance() {
    return chance;
  }

  public int getChanceIncrease() {
    return chanceIncrease;
  }

  public List<String> getDiscription() {
    return getEnchantment().getInfoDescription();
  }

  public String getBookColor() {
    return CC.translate(getEnchantment().getBookColor());
  }

  public String getEnchantmentColor() {
    return CC.translate(getEnchantment().getColor());
  }

  public EnchantmentType getType() {
    if (getEnchantment() == null || getEnchantment().getEnchantmentType() == null) {
      return EnchantmentType.getFromName(typeName);
    } else {
      return getEnchantment().getEnchantmentType();
    }
  }

  public boolean isActivated() {
    return getEnchantment() != null && getEnchantment().isActivated();
  }

  public CEnchantment getEnchantment() {
    if (cachedEnchantment == null) {
      cachedEnchantment = enchantmentsManager.getEnchantmentFromName(name);
    }
    return cachedEnchantment;
  }

  public int getLevel(ItemStack item) {
    return getEnchantment().getLevel(item);
  }

  public boolean chanceSuccessful() {
    return chance >= 100 || chance <= 0 || (new Random().nextInt(100) + 1) <= chance;
  }

  public boolean chanceSuccessful(ItemStack item) {
    return enchantmentsManager.getEnchantmentFromName(name).chanceSuccesful(getLevel(item));
  }

  public boolean hasChanceSystem() {
    return hasChanceSystem;
  }
}