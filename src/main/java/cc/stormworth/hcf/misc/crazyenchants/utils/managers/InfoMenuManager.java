package cc.stormworth.hcf.misc.crazyenchants.utils.managers;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager.Files;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.EnchantmentType;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InfoMenuManager {

  public static final InfoMenuManager instance = new InfoMenuManager();
  private Inventory inventoryMenu;
  private String inventoryName;
  private int inventorySize;
  private ItemStack backRight;
  private ItemStack backLeft;
  private final List<EnchantmentType> enchantmentTypes = new ArrayList<>();
  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();

  public static InfoMenuManager getInstance() {
    return instance;
  }

  public void load() {
    enchantmentTypes.clear();
    FileConfiguration file = Files.CUSTOMENCHANTS.getFile();
    String path = "Info-GUI-Settings";
    inventoryName = CC.translate(file.getString(path + ".Inventory.Name", "&c&lEnchantment Info"));
    inventorySize = file.getInt(path + ".Inventory.Size", 18);
    inventoryMenu = Bukkit.createInventory(null, inventorySize, inventoryName);
    backRight = ItemBuilder.of(
            Material.valueOf(file.getString(path + ".Back-Item.Right.Item", "NETHER_STAR")))
        .name(file.getString(path + ".Back-Item.Right.Name", "&7&l<<&6&lBack"))
        .setLore(file.getStringList(path + ".Back-Item.Right.Lore"))
        .build();
    backLeft = ItemBuilder.of(
            Material.valueOf(file.getString(path + ".Back-Item.Left.Item", "NETHER_STAR")))
        .name(file.getString(path + ".Back-Item.Left.Name", "&6&lBack&7&l>>"))
        .setLore(file.getStringList(path + ".Back-Item.Left.Lore"))
        .build();
    for (String type : file.getConfigurationSection("Types").getKeys(false)) {
      EnchantmentType enchantmentType = new EnchantmentType(type);
      enchantmentTypes.add(enchantmentType);
      inventoryMenu.setItem(enchantmentType.getSlot(), enchantmentType.getDisplayItem());
    }
  }

  public Inventory getInventoryMenu() {
    return inventoryMenu;
  }

  public String getInventoryName() {
    return inventoryName;
  }

  public int getInventorySize() {
    return inventorySize;
  }

  public List<EnchantmentType> getEnchantmentTypes() {
    return enchantmentTypes;
  }

  public ItemStack getBackRightButton() {
    return backRight;
  }

  public ItemStack getBackLeftButton() {
    return backLeft;
  }

  public EnchantmentType getFromName(String name) {
    for (EnchantmentType enchantmentType : enchantmentTypes) {
      if (enchantmentType.getName().equalsIgnoreCase(name)) {
        return enchantmentType;
      }
    }
    return null;
  }

  public void openInfoMenu(Player player) {
    player.openInventory(inventoryMenu);
  }

  public void openInfoMenu(Player player, EnchantmentType enchantmentType) {
    List<CEnchantment> enchantments = enchantmentType.getEnchantments();
    int slots = 9;
      for (int size = enchantments.size() + 1; size > 9; size -= 9) {
          slots += 9;
      }
    Inventory inventory = Bukkit.createInventory(null, slots, inventoryName);
    for (CEnchantment enchantment : enchantments) {
      if (enchantment.isActivated()) {
        inventory.addItem(
            enchantmentsManager.getEnchantmentBook()
                .name(enchantment.getInfoName())
                .setLore(enchantment.getInfoDescription())
                .build());
      }
    }
    inventory.setItem(slots - 1, backRight);
    player.openInventory(inventory);
  }

}