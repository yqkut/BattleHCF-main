package cc.stormworth.hcf.misc.crazyenchants.utils.objects;


import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class LostBook {

    private final int slot;
    private final boolean inGUI;
    private final ItemBuilder displayItem;
    private final int cost;
    private final boolean useSound;
    private Sound sound;

    public LostBook(int slot, boolean inGUI, ItemBuilder displayItem, int cost, boolean useSound, String sound) {
        this.slot = slot - 1;
        this.inGUI = inGUI;
        this.displayItem = displayItem;
        this.cost = cost;
        try {
            this.sound = Sound.valueOf(sound);
        } catch (Exception e) {
            this.sound = null;
        }
        this.useSound = sound != null && useSound;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isInGUI() {
        return inGUI;
    }

    public ItemBuilder getDisplayItem() {
        return displayItem;
    }

    public int getCost() {
        return cost;
    }

    public boolean playSound() {
        return useSound;
    }

    public Sound getSound() {
        return sound;
    }

    public ItemStack getLostBook(Category category) {
        return getLostBook(category, 1);
    }

    public ItemStack getLostBook(Category category, int amount) {
        FileConfiguration file = FileManager.Files.CUSTOMENCHANTS.getFile();
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Category%", category.getDisplayItem().build().getItemMeta().getDisplayName());
        return ItemBuilder.of(Material.valueOf(file.getString("Settings.LostBook.Item"))).amount(amount).name(file.getString("Settings.LostBook.Name")).setLore(file.getStringList("Settings.LostBook.Lore")).build();
    }
}