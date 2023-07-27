package cc.stormworth.hcf.misc.crazyenchants;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.controllers.EnchantmentControl;
import cc.stormworth.hcf.misc.crazyenchants.controllers.InfoGUIControl;
import cc.stormworth.hcf.misc.crazyenchants.controllers.ShopControl;
import cc.stormworth.hcf.misc.crazyenchants.enchantments.EnchantmentsTask;
import cc.stormworth.hcf.misc.crazyenchants.processors.CEnchantmentType;
import cc.stormworth.hcf.misc.crazyenchants.processors.Methods;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.ShopOption;
import cc.stormworth.hcf.misc.crazyenchants.utils.managers.InfoMenuManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.managers.ShopManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter
public class EnchantmentsManager {

  private final Main plugin;
  private final FileManager fileManager = FileManager.getInstance();
  private ShopManager shopManager;
  private final List<Category> categories = new ArrayList<>();
  private final List<CEnchantment> registeredEnchantments = new ArrayList<>();

  private boolean useUnsafeEnchantments;
  private boolean enchantStackedItems;
  private ItemBuilder enchantmentBook;
  private InfoMenuManager infoMenuManager;

  public EnchantmentsManager(Main plugin) {
    this.plugin = plugin;

  }

  public void setupCrazyEnchants() {
    fileManager.logInfo(false).setup(plugin);
    reload();

    rCommandHandler.registerParameterType(CEnchantment.class, new CEnchantmentType());

    PluginManager pluginManager = Bukkit.getServer().getPluginManager();

    pluginManager.registerEvents(new ShopControl(), plugin);
    pluginManager.registerEvents(new InfoGUIControl(), plugin);
    pluginManager.registerEvents(new EnchantmentControl(), plugin);

    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new EnchantmentsTask(),  20L, 20L);
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new EnchantmentRunnable(this),  0L, 5L);
  }

  public void reload() {
    registeredEnchantments.clear();
    categories.clear();
    infoMenuManager = InfoMenuManager.getInstance();
    infoMenuManager.load();
    CEnchantments.invalidateCachedEnchants();
    FileConfiguration config = FileManager.Files.CUSTOMENCHANTS.getFile();
    FileConfiguration enchants = FileManager.Files.CUSTOMENCHANTS.getFile();

    enchantmentBook = ItemBuilder.of(Material.valueOf(config.getString("Settings.Enchantment-Book-Item")));
    useUnsafeEnchantments = config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments");
    enchantStackedItems = config.contains("Settings.EnchantmentOptions.Enchant-Stacked-Items") &&
            config.getBoolean("Settings.EnchantmentOptions.Enchant-Stacked-Items");

    for (String category : config.getConfigurationSection("Categories").getKeys(false)) {
      String path = "Categories." + category;

      LostBook lostBook = new LostBook(
          config.getInt(path + ".LostBook.Slot"),
          config.getBoolean(path + ".LostBook.InGUI"),
          ItemBuilder.of(Material.valueOf(config.getString(path + ".LostBook.Item")))
              .name(config.getString(path + ".LostBook.Name").replace("%category%", category))
              .setLore(config.getStringList(path + ".LostBook.Lore")),
          config.getInt(path + ".LostBook.Cost"),
          config.getBoolean(path + ".LostBook.Sound-Toggle"),
          config.getString(path + ".LostBook.Sound"));

      categories.add(new Category(
          category,
          config.getInt(path + ".Slot"),
          config.getBoolean(path + ".InGUI"),
          ItemBuilder.of(Material.valueOf(config.getString(path + ".Item")))
              .name(config.getString(path + ".Name"))
              .data((short) config.getInt(path + ".Data"))
              .setLore(config.getStringList(path + ".Lore")),
          config.getInt(path + ".Cost"),
          config.getInt(path + ".Rarity"),
          lostBook,
          config.getInt(path + ".EnchOptions.SuccessPercent.Max"),
          config.getInt(path + ".EnchOptions.SuccessPercent.Min"),
          config.getInt(path + ".EnchOptions.DestroyPercent.Max"),
          config.getInt(path + ".EnchOptions.DestroyPercent.Min"),
          config.getBoolean(path + ".EnchOptions.MaxLvlToggle"),
          config.getInt(path + ".EnchOptions.LvlRange.Max"),
          config.getInt(path + ".EnchOptions.LvlRange.Min")));

    }
    for (CEnchantments cEnchantment : CEnchantments.values()) {
      String name = cEnchantment.getName();
      String path = "Enchantments." + name;
      if (enchants.contains(path)) {
        CEnchantment enchantment = new CEnchantment(name)
            .setCustomName(enchants.getString(path + ".Name"))
            .setActivated(enchants.getBoolean(path + ".Enabled"))
            .setColor(enchants.getString(path + ".Color"))
            .setBookColor(enchants.getString(path + ".BookColor"))
            .setMaxLevel(enchants.getInt(path + ".MaxPower"))
            .setEnchantmentType(cEnchantment.getType())
            .setInfoName(enchants.getString(path + ".Info.Name"))
            .setInfoDescription(enchants.getStringList(path + ".Info.Description"))
            .setCategories(enchants.getStringList(path + ".Categories"))
            .setChance(cEnchantment.getChance())
            .setChanceIncrease(cEnchantment.getChanceIncrease());
        if (enchants.contains(path + ".Enchantment-Type")) {
          enchantment.setEnchantmentType(EnchantmentType.getFromName(enchants.getString(path + ".Enchantment-Type")));
        }
        if (cEnchantment.hasChanceSystem()) {
          if (enchants.contains(path + ".Chance-System.Base")) {
            enchantment.setChance(enchants.getInt(path + ".Chance-System.Base"));
          } else {
            enchantment.setChance(cEnchantment.getChance());
          }
          if (enchants.contains(path + ".Chance-System.Increase")) {
            enchantment.setChanceIncrease(enchants.getInt(path + ".Chance-System.Increase"));
          } else {
            enchantment.setChanceIncrease(cEnchantment.getChanceIncrease());
          }
        }
        enchantment.registerEnchantment();
      }
    }
    ShopOption.loadShopOptions();
    shopManager = ShopManager.getInstance();
    shopManager.load();
  }

  public Map<CEnchantments, HashMap<PotionEffectType, Integer>> getEnchantmentPotions() {
    HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> enchants = new HashMap<>();

    enchants.put(CEnchantments.INFRARED, new HashMap<>());
    enchants.get(CEnchantments.INFRARED).put(PotionEffectType.NIGHT_VISION, -1);

    enchants.put(CEnchantments.MERMAID, new HashMap<>());
    enchants.get(CEnchantments.MERMAID).put(PotionEffectType.WATER_BREATHING, -1);

    enchants.put(CEnchantments.FIRERESISTANCE, new HashMap<>());
    enchants.get(CEnchantments.FIRERESISTANCE).put(PotionEffectType.FIRE_RESISTANCE, -1);

    enchants.put(CEnchantments.TURTLESHELL, new HashMap<>());
    enchants.get(CEnchantments.TURTLESHELL).put(PotionEffectType.WATER_BREATHING, -1);

    enchants.put(CEnchantments.SPECTRAL, new HashMap<>());
    enchants.get(CEnchantments.SPECTRAL).put(PotionEffectType.INVISIBILITY, -1);

    enchants.put(CEnchantments.SPEED, new HashMap<>());
    enchants.get(CEnchantments.SPEED).put(PotionEffectType.SPEED, -1);
    return enchants;
  }

  public CEBook getCEBook(ItemStack book) {
    try {
      return new CEBook(getEnchantmentBookEnchantment(book),
          getBookLevel(book, getEnchantmentBookEnchantment(book)), book.getAmount());
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isEnchantmentBook(ItemStack book) {
    if (book != null && book.getType() == enchantmentBook.build().getType() && book.hasItemMeta()
        && book.getItemMeta().hasDisplayName()) {
      for (CEnchantment enchantment : registeredEnchantments) {
        String bookNameCheck = book.getItemMeta().getDisplayName();
        String[] split = bookNameCheck.split(" ");
        if (bookNameCheck.replace(" " + split[split.length - 1], "")
            .equals(enchantment.getBookColor() + enchantment.getCustomName())) {
          return true;
        }
      }
    }
    return false;
  }

  public CEnchantment getEnchantmentBookEnchantment(ItemStack book) {
    if (book != null && book.getType() == enchantmentBook.build().getType() && book.hasItemMeta()
        && book.getItemMeta().hasDisplayName()) {
      for (CEnchantment enchantment : registeredEnchantments) {
        String bookNameCheck = book.getItemMeta().getDisplayName();
        String[] split = bookNameCheck.split(" ");
        if (bookNameCheck.replace(" " + split[split.length - 1], "")
            .equals(enchantment.getBookColor() + enchantment.getCustomName())) {
          return enchantment;
        }
      }
    }
    return null;
  }

  public int getBookLevel(ItemStack book, CEnchantment enchant) {
    return convertLevelInteger(book.getItemMeta().getDisplayName()
        .replace(enchant.getBookColor() + enchant.getCustomName() + " ", ""));
  }

  public int getLevel(ItemStack item, CEnchantment enchant) {
    String line = "";
    if (Methods.verifyItemLore(item)) {
      ItemMeta meta = item.getItemMeta();
      if (meta != null && meta.hasLore()) {
        List<String> itemLore = meta.getLore();
        if (itemLore != null) {
          for (String lore : itemLore) {
            if (lore.contains(enchant.getCustomName())) {
              line = lore;
              break;
            }
          }
        }
      }
    }
    int level = convertLevelInteger(
        line.replace(enchant.getColor() + enchant.getCustomName() + " ", ""));
    if (!useUnsafeEnchantments && level > enchant.getMaxLevel()) {
      level = enchant.getMaxLevel();
    }
    return level;
  }

  public int getLevel(ItemStack item, CEnchantments enchant) {
    int level;

    String line = "";
    if (Methods.verifyItemLore(item)) {
      ItemMeta meta = item.getItemMeta();
      if (meta != null && meta.hasLore()) {
        List<String> itemLore = meta.getLore();
        if (itemLore != null) {
          for (String lore : itemLore) {
            if (lore.contains(enchant.getCustomName())) {
              line = lore;
              break;
            }
          }
        }
      }
    }

    level = convertLevelInteger(line.replace(enchant.getEnchantment().getColor() + enchant.getCustomName() + " ", ""));

    if (!useUnsafeEnchantments && level > enchant.getEnchantment().getMaxLevel()) {
      level = enchant.getEnchantment().getMaxLevel();
    }

    return level;
  }

  public int randomLevel(CEnchantment enchantment, Category category) {
    int enchantmentMax = enchantment.getMaxLevel();
    int randomLevel = 1 + CorePlugin.RANDOM.nextInt(enchantmentMax);
    if (category.useMaxLevel()) {
      if (randomLevel > category.getMaxLevel()) {
        randomLevel = 1 + CorePlugin.RANDOM.nextInt(enchantmentMax);
      }
      if (randomLevel < category.getMinLevel()) {
        randomLevel = category.getMinLevel();
      }
      if (randomLevel > enchantmentMax) {
        randomLevel = enchantmentMax;
      }
    }
    return randomLevel;
  }

  public String convertLevelString(int i) {
    switch (i) {
      case 0:
      case 1:
        return "I";
      case 2:
        return "II";
      case 3:
        return "III";
      case 4:
        return "IV";
      case 5:
        return "V";
      case 6:
        return "VI";
      case 7:
        return "VII";
      case 8:
        return "VIII";
      case 9:
        return "IX";
      case 10:
        return "X";
      default:
        return i + "";

    }
  }

  public int convertLevelInteger(String i) {
    switch (i) {
      case "I":
        return 1;
      case "II":
        return 2;
      case "III":
        return 3;
      case "IV":
        return 4;
      case "V":
        return 5;
      case "VI":
        return 6;
      case "VII":
        return 7;
      case "VIII":
        return 8;
      case "IX":
        return 9;
      case "X":
        return 10;
      default:
        if (Methods.isInt(i)) {
          return Integer.parseInt(i);
        } else {
          return 0;
        }
    }
  }

  public Material getMaterial(String oldMaterial) {
    return Material.getMaterial(oldMaterial.toUpperCase());
  }


  public ItemStack getEnchantmentBookItem() {
    return enchantmentBook.build();
  }

  public boolean hasEnchantments(ItemStack item) {
    for (CEnchantment enchantment : registeredEnchantments) {
      if (hasEnchantment(item, enchantment)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasEnchantment(ItemStack item, CEnchantment enchantment) {
    if (Methods.verifyItemLore(item)) {
      ItemMeta meta = item.getItemMeta();
      List<String> itemLore = meta.getLore();

      if (enchantment.isActivated() && itemLore != null) {
        for (String lore : itemLore) {

          String[] split = lore.split(" ");
          if (lore.replace(" " + split[split.length - 1], "").equals(enchantment.getColor() + enchantment.getCustomName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean hasEnchantment(ItemStack item, CEnchantments enchantment) {
    return hasEnchantment(item, enchantment.getEnchantment());
  }

  public Category getHighestEnchantmentCategory(CEnchantment enchantment) {
    Category topCategory = null;
    int rarity = 0;
    for (Category category : enchantment.getCategories()) {
      if (category.getRarity() >= rarity) {
        rarity = category.getRarity();
        topCategory = category;
      }
    }
    return topCategory;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public Category getCategory(String name) {
    for (Category category : categories) {
      if (category.getName().equalsIgnoreCase(name)) {
        return category;
      }
    }
    return null;
  }

  public Category getCategoryFromLostBook(ItemStack item) {
    for (Category category : categories) {
      if (item.isSimilar(category.getLostBook().getLostBook(category))) {
        return category;
      }
    }
    return null;
  }

  public CEBook getRandomEnchantmentBook(Category category) {
    try {
      List<CEnchantment> enchantments = category.getEnabledEnchantments();
      CEnchantment enchantment = enchantments.get(CorePlugin.RANDOM.nextInt(enchantments.size()));
      return new CEBook(enchantment, randomLevel(enchantment, category), 1, category);
    } catch (Exception e) {
      System.out.println("[CE]>> The category " + category.getName() + " has no enchantments."
          + " Please add enchantments to the category in the Enchantments.yml. If you don't wish to have the category feel free to delete it from the Config.yml.");
      return null;
    }
  }

  public List<CEnchantment> getRegisteredEnchantments() {
    return new ArrayList<>(registeredEnchantments);
  }

  public CEnchantment getEnchantmentFromName(String enchantmentString) {
    enchantmentString = Methods.stripString(enchantmentString);
    for (CEnchantment enchantment : registeredEnchantments) {
      if (Methods.stripString(enchantment.getName()).equalsIgnoreCase(enchantmentString) ||
          Methods.stripString(enchantment.getCustomName()).equalsIgnoreCase(enchantmentString)) {
        return enchantment;
      }
    }
    return null;
  }

  public void registerEnchantment(CEnchantment enchantment) {
    registeredEnchantments.add(enchantment);
  }

  public void unregisterEnchantment(CEnchantment enchantment) {
    registeredEnchantments.remove(enchantment);
  }

  public ItemStack addEnchantment(ItemStack item, CEnchantment enchantment, int level) {
    Map<CEnchantment, Integer> enchantments = new HashMap<>();
    enchantments.put(enchantment, level);
    return addEnchantments(item, enchantments);
  }

  public ItemStack addEnchantments(ItemStack item, Map<CEnchantment, Integer> enchantments) {
    for (Map.Entry<CEnchantment, Integer> entry : enchantments.entrySet()) {
      CEnchantment enchantment = entry.getKey();
      int level = entry.getValue();
      if (hasEnchantment(item, enchantment)) {
        removeEnchantment(item, enchantment);
      }
      List<String> newLore = new ArrayList<>();
      List<String> lores = new ArrayList<>();
      HashMap<String, String> enchantmentStrings = new HashMap<>();
      for (CEnchantment en : getEnchantmentsOnItem(item)) {
        enchantmentStrings.put(en.getName(), CC.translate(
            en.getColor() + en.getCustomName() + " " + convertLevelString(getLevel(item, en))));
        removeEnchantment(item, en);
      }
      ItemMeta meta = item.getItemMeta();
      if (meta != null && meta.hasLore()) {
        List<String> itemLore = meta.getLore();
        if (itemLore != null) {
          lores.addAll(itemLore);
        }
      }
      enchantmentStrings.put(enchantment.getName(), CC.translate(
          enchantment.getColor() + enchantment.getCustomName() + " " + convertLevelString(level)));
      for (Map.Entry<String, String> stringEntry : enchantmentStrings.entrySet()) {
        newLore.add(stringEntry.getValue());
      }
      newLore.addAll(lores);
      if (meta != null) {
        meta.setLore(newLore);
      }
      item.setItemMeta(meta);
    }
    return item;
  }

  public ItemStack removeEnchantment(ItemStack item, CEnchantment enchant) {
    List<String> newLore = new ArrayList<>();
    ItemMeta meta = item.getItemMeta();
    if (meta != null && meta.hasLore()) {
      List<String> itemLore = meta.getLore();
      if (itemLore != null) {
        for (String lore : itemLore) {
          if (!lore.contains(enchant.getCustomName())) {
            newLore.add(lore);
          }
        }
      }
    }
    if (meta != null) {
      meta.setLore(newLore);
    }
    item.setItemMeta(meta);
    return item;
  }

  public List<CEnchantment> getEnchantmentsOnItem(ItemStack item) {
    return new ArrayList<>(getEnchantments(item).keySet());
  }

  public Map<CEnchantment, Integer> getEnchantments(ItemStack item) {
    if (!Methods.verifyItemLore(item)) {
      return Collections.emptyMap();
    }
    List<String> lore = item.getItemMeta().getLore();
    Map<CEnchantment, Integer> enchantments = null;
    for (String line : lore) {
      int lastSpaceIndex = line.lastIndexOf(' ');
      if (lastSpaceIndex < 1 || lastSpaceIndex + 1 > line.length()) {
        continue;
      }
      String enchantmentName = line.substring(0, lastSpaceIndex);
      for (CEnchantment enchantment : registeredEnchantments) {
        if (!enchantment.isActivated()) {
          continue;
        }
        if (!enchantmentName.equals(enchantment.getColor() + enchantment.getCustomName())) {
          continue;
        }
        String levelString = line.substring(lastSpaceIndex + 1);
        int level = convertLevelInteger(levelString);
        if (level < 1) {
          break;
        }
        if (enchantments == null) {
          enchantments = new HashMap<>();
        }
        enchantments.put(enchantment, level);
        break;
      }
    }
    if (enchantments == null) {
      enchantments = Collections.emptyMap();
    }
    return enchantments;
  }

  public Map<PotionEffectType, Integer> getUpdatedEffects(Player player, ItemStack includedItem, ItemStack excludedItem, CEnchantments enchantment) {

    HashMap<PotionEffectType, Integer> effects = new HashMap<>();
    List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getEquipment().getArmorContents()));

    if (includedItem == null) {
      includedItem = new ItemStack(Material.AIR);
    }

    if (excludedItem == null) {
      excludedItem = new ItemStack(Material.AIR);
    }

    if (excludedItem.isSimilar(includedItem)) {
      excludedItem = new ItemStack(Material.AIR);
    }

    items.add(includedItem);

    Map<PotionEffectType, Integer> enchantEffects = getEnchantmentPotions().get(enchantment);

    if (enchantEffects == null) {
      return effects;
    }

    for (ItemStack armor : items) {
      if (armor != null && !armor.isSimilar(excludedItem) && hasEnchantment(armor, enchantment.getEnchantment())) {

        int level = getLevel(armor, enchantment.getEnchantment());
        if (!useUnsafeEnchantments && level > enchantment.getEnchantment().getMaxLevel()) {
          level = enchantment.getEnchantment().getMaxLevel();
        }

        for (PotionEffectType type : enchantEffects.keySet()) {
          if (enchantEffects.containsKey(type)) {
            if (effects.containsKey(type)) {
              int updated = effects.get(type);
              if (updated < (level + enchantEffects.get(type))) {
                effects.put(type, level + enchantEffects.get(type));
              }
            } else {
              effects.put(type, level + enchantEffects.get(type));
            }
          }
        }
      }
    }


/*    Map<CEnchantments, HashMap<PotionEffectType, Integer>> armorEffects = getEnchantmentPotions();

    for (Map.Entry<CEnchantments, HashMap<PotionEffectType, Integer>> enchantments : armorEffects.entrySet()) {

    }*/

    for (PotionEffectType type : enchantEffects.keySet()) {
      if (!effects.containsKey(type)) {
        effects.put(type, -1);
      }
    }
    return effects;
  }
}