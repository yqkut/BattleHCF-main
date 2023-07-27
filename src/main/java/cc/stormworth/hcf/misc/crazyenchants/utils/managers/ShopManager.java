package cc.stormworth.hcf.misc.crazyenchants.utils.managers;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.EnchantmentsManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager.Files;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.ShopOption;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.Category;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.LostBook;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShopManager {

  private static final ShopManager instance = new ShopManager();
  int[] glassslots = new int[]{0, 1, 7, 8, 9, 17, 27, 35, 36, 37, 43, 44};
  private final EnchantmentsManager enchantmentsManager = Main.getInstance()
      .getEnchantmentsManager();
  private String inventoryName;
  private int inventorySize;
  private final HashMap<ItemBuilder, Integer> customizerItems = new HashMap<>();
  private final HashMap<ItemBuilder, Integer> shopItems = new HashMap<>();

  public static ShopManager getInstance() {
    return instance;
  }

  public void load() {
    customizerItems.clear();
    shopItems.clear();
    FileConfiguration config = Files.CUSTOMENCHANTS.getFile();
    inventoryName = CC.translate("Enchantments");
    inventorySize = config.getInt("Settings.GUISize");
    for (int slot : glassslots) {
      customizerItems.put(ItemBuilder.of(Material.STAINED_GLASS_PANE).data((short) 1).name(" "),
          slot);
    }
    for (Category category : enchantmentsManager.getCategories()) {
      if (category.isInGUI()) {
        if (category.getSlot() > inventorySize) {
          continue;
        }
        shopItems.put(category.getDisplayItem(), category.getSlot());
      }
      LostBook lostBook = category.getLostBook();
      if (lostBook.isInGUI()) {
        if (lostBook.getSlot() > inventorySize) {
          continue;
        }
        shopItems.put(lostBook.getDisplayItem(), lostBook.getSlot());
      }
    }
    for (ShopOption option : ShopOption.values()) {
      if (option.isInGUI()) {
        if (option.getSlot() > inventorySize) {
          continue;
        }
        shopItems.put(option.getItemBuilder(), option.getSlot());
      }
    }
  }

  public Inventory getShopInventory(Player player) {
    Inventory inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
    for (Entry<ItemBuilder, Integer> itemBuilders : customizerItems.entrySet()) {
      //itemBuilders.getKey().setNamePlaceholders(placeholders).setLorePlaceholders(placeholders);
      inventory.setItem(itemBuilders.getValue(), itemBuilders.getKey().build());
    }
    shopItems.keySet()
        .forEach(itemBuilder -> inventory.setItem(shopItems.get(itemBuilder), itemBuilder.build()));
    return inventory;
  }

  public String getInventoryName() {
    return inventoryName;
  }

  public int getInventorySize() {
    return inventorySize;
  }
}