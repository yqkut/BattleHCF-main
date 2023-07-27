package cc.stormworth.hcf.misc.crazyenchants.utils.enums;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.core.util.item.ItemUtils;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@AllArgsConstructor
public enum ShopOption {

    GKITZ("GKitz", "GKitz", "Name", "Lore", false),
    BLACKSMITH("BlackSmith", "BlackSmith", "Name", "Lore", false),
    TINKER("Tinker", "Tinker", "Name", "Lore", false),
    INFO("Info", "Info", "Name", "Lore", false),

    PROTECTION_CRYSTAL("ProtectionCrystal", "ProtectionCrystal", "GUIName", "GUILore", true),
    SUCCESS_DUST("SuccessDust", "Dust.SuccessDust", "GUIName", "GUILore", true),
    DESTROY_DUST("DestroyDust", "Dust.DestroyDust", "GUIName", "GUILore", true),
    SCRAMBLER("Scrambler", "Scrambler", "GUIName", "GUILore", true),

    BLACK_SCROLL("BlackScroll", "BlackScroll", "GUIName", "Lore", true),
    WHITE_SCROLL("WhiteScroll", "WhiteScroll", "GUIName", "Lore", true),
    TRANSMOG_SCROLL("TransmogScroll", "TransmogScroll", "GUIName", "Lore", true);

    private static final HashMap<ShopOption, Option> shopOptions = new HashMap<>();
    private String optionPath;
    private String path;
    private String namePath;
    private String lorePath;
    private boolean buyable;

    public static void loadShopOptions() {
        FileConfiguration config = FileManager.Files.CUSTOMENCHANTS.getFile();
        shopOptions.clear();
        for (ShopOption shopOption : values()) {
            String itemPath = "Settings." + shopOption.getPath() + ".";
            String costPath = "Settings.Costs." + shopOption.getOptionPath() + ".";
            try {
                ItemBuilder item = ItemBuilder.of(Material.valueOf(config.getString(itemPath + "Item")))
                        .name(config.getString(itemPath + shopOption.getNamePath()))
                        .setLore(config.getStringList(itemPath + shopOption.getLorePath()));
                if (config.getBoolean(itemPath + "Glowing")) item.enchant(ItemUtils.FAKE_GLOW, 2);
                shopOptions.put(shopOption, new Option(item,
                        config.getInt(itemPath + "Slot", 1) - 1,
                        config.getBoolean(itemPath + "InGUI"),
                        config.getInt(costPath + "Cost", 100)));
            } catch (Exception e) {
                System.out.println("The option " + shopOption.getOptionPath() + " has failed to load.");
                e.printStackTrace();
            }
        }
    }

    public ItemStack getItem() {
        return getItemBuilder().build();
    }

    public ItemBuilder getItemBuilder() {
        return shopOptions.get(this).getItemBuilder();
    }

    public int getSlot() {
        return shopOptions.get(this).getSlot();
    }

    public boolean isInGUI() {
        return shopOptions.get(this).isInGUI();
    }

    public int getCost() {
        return shopOptions.get(this).getCost();
    }

    private String getOptionPath() {
        return optionPath;
    }

    private String getPath() {
        return path;
    }

    private String getNamePath() {
        return namePath;
    }

    private String getLorePath() {
        return lorePath;
    }

    public boolean isBuyable() {
        return buyable;
    }

    private static class Option {

        private final ItemBuilder itemBuilder;
        private final int slot;
        private final boolean inGUI;
        private final int cost;

        public Option(ItemBuilder itemBuilder, int slot, boolean inGUI, int cost) {
            this.itemBuilder = itemBuilder;
            this.slot = slot;
            this.inGUI = inGUI;
            this.cost = cost;
        }

        public ItemBuilder getItemBuilder() {
            return itemBuilder;
        }

        public int getSlot() {
            return slot;
        }

        public boolean isInGUI() {
            return inGUI;
        }

        public int getCost() {
            return cost;
        }
    }

}