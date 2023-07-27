package cc.stormworth.hcf.misc.crazyenchants.utils.objects;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.misc.crazyenchants.utils.FileManager;
import cc.stormworth.hcf.misc.crazyenchants.utils.managers.InfoMenuManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentType {

    private final String name;
    private final int slot;
    private final ItemStack displayItem;
    private final List<CEnchantment> enchantments = new ArrayList<>();
    private final List<Material> enchantableMaterials = new ArrayList<>();

    public EnchantmentType(String name) {
        FileConfiguration file = FileManager.Files.CUSTOMENCHANTS.getFile();
        String path = "Types." + name;
        this.name = name;
        this.slot = file.getInt(path + ".Display-Item.Slot", 1) - 1;
        this.displayItem = ItemBuilder.of(Material.valueOf(file.getString(path + ".Display-Item.Item")))
                .data((short) file.getInt(path + ".Display-Item.Data"))
                .name(file.getString(path + ".Display-Item.Name"))
                .setLore(file.getStringList(path + ".Display-Item.Lore")).build();
        for (String type : file.getStringList(path + ".Enchantable-Items")) {
            Material material = Material.valueOf(type);
            if (material != null) {
                this.enchantableMaterials.add(material);
            }
        }
    }

    public static EnchantmentType getFromName(String name) {
        return InfoMenuManager.getInstance().getFromName(name);
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public List<Material> getEnchantableMaterials() {
        return enchantableMaterials;
    }

    public boolean canEnchantItem(ItemStack item) {
        return enchantableMaterials.contains(item.getType());
    }

    public List<CEnchantment> getEnchantments() {
        return enchantments;
    }

    public void addEnchantment(CEnchantment enchantment) {
        enchantments.add(enchantment);
    }

    public void removeEnchantment(CEnchantment enchantment) {
        enchantments.remove(enchantment);
    }

}