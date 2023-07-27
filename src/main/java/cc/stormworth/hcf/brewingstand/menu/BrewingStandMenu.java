package cc.stormworth.hcf.brewingstand.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.brewingstand.BrewingStand;
import cc.stormworth.hcf.brewingstand.PotionCategory;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.menu.ConfirmMenu;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class BrewingStandMenu extends Menu {

    private final BrewingStand brewingStand;

    {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "&eBrewing Stand";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 3 * 9;
    }

    @Override
    public void onOpen(Player player) {
        HCFProfile profile = HCFProfile.get(player);

        profile.setOpenBrewingStand(brewingStand);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(3, Button.fromItem(new ItemBuilder(brewingStand.isActive() ? Material.LEVER : Material.REDSTONE_TORCH_ON)
                .name("&6&lStatus")
                .addToLore(
                        (brewingStand.isActive() ? "&6➞ " : "") + "&aON",
                        (!brewingStand.isActive() ? "&6➞ " : "") + "&cOFF",
                        "",
                        "&eClick to change!"
                ).build(), (other) -> {
            brewingStand.setActive(!brewingStand.isActive());
        }));

        buttons.put(5, Button.fromItem(new ItemBuilder(Material.CAULDRON_ITEM)
                .name("&6&lResources Storage")
                .addToLore(
                        "",
                        "&7Join your storage and put on, all resources needed",
                        "",
                        "&eClick to check!"
                ).build(), other -> {

            if (brewingStand.getActiveCategory() == null){
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                player.sendMessage(CC.translate("&ePlease choose a Potion Category before accessing to this Storage."));
                return;
            }

            new ResourcesMenu(brewingStand).openMenu(other);
        }));

        int amountOfBottles = brewingStand.getAmountOf(Material.POTION);

        buttons.put(4, Button.fromItem(new ItemBuilder(Material.WATER_BUCKET).name("&6&lGlass refill")
                .addToLore(
                        "",
                        "&e&l➞ " + (amountOfBottles > 3 ? "&a" : "&c" ) + "Glass Bottle &7(" + (amountOfBottles > 3 ? "&a✔" : "&c✘") + "&7) &e" + amountOfBottles + " total.",
                        "",
                        "&eClick to deposit bottles!"
                ).build(), other -> {
            Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Glass Bottles");

            if (!brewingStand.getResources().containsKey(Material.POTION)){
                brewingStand.getResources().put(Material.POTION, new CopyOnWriteArrayList<>());
            }

            List<ItemStack> resources = brewingStand.getResources().get(Material.POTION);

            for (ItemStack stack : resources){
                inventory.addItem(stack);
            }

            player.openInventory(inventory);
        }));

        /*buttons.put(8, Button.fromItem(new ItemBuilder(Material.GOLD_NUGGET)
                .name("&6&lHologram Visibility")
                .addToLore(
                        "",
                        "&e&l➞ &cInvisible",
                        "",
                        "&eClick to toggle!"
                ).build()));*/

        ItemBuilder builder = new ItemBuilder(Material.POTION);

        List<String> potionsLore = Lists.newArrayList();

        potionsLore.add("");
        potionsLore.add("&7Choose the &6&lPotion Category&7, you'd like to choose");
        potionsLore.add("");
        potionsLore.add("&6&lPotions: ");

        for (PotionCategory potionCategory : PotionCategory.values()) {
            if (brewingStand.getActiveCategory() == potionCategory){
                potionsLore.add("&e&l➞ &a" + potionCategory.getName() + " &7(Current)");
            }else {
                potionsLore.add("&e&l➞ &c" + potionCategory.getName());
            }
        }

        if (brewingStand.getActiveCategory() == null){
            builder.setGlowing(true);
        }

        potionsLore.add("");
        potionsLore.add("&eClick to choose!");

        buttons.put(getSlot(0, 1), Button.fromItem(builder
                .name("&6&lPotion Category")
                .setLore(potionsLore)
                .build(), other -> new PotionCategoryMenu(brewingStand).openMenu(player)));

        if (brewingStand.getActiveCategory() != null && brewingStand.getStatus() == BrewingStand.Status.BREWING) {
            buttons.put(getSlot(3, 1), Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(5)
                    .name("&aProcessing potions")
                    .amount(brewingStand.getSecondsBrewing())
                    .build()));
        }

        if (brewingStand.getActiveCategory() != null){
            buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(brewingStand.getActiveCategory().getItemStack().clone())
                    .addToLore(
                            "",
                            "&f" + brewingStand.getCurrentPotionsCount(),
                            "",
                            "&6➞ " + brewingStand.getStatus().getName()
                    )
                    .build()));
        }

        if (brewingStand.getActiveCategory() != null && brewingStand.getStatus() == BrewingStand.Status.BREWING) {
            buttons.put(getSlot(5, 1), Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(5)
                    .name("&aProcessing potions")
                    .amount(brewingStand.getSecondsBrewing())
                    .build()));
        }

        buttons.put(getSlot(8, 2), Button.fromItem(new ItemBuilder(Material.TNT)
                .name("&c&lDestroy machine")
                .addToLore(
                        "",
                        "&7By destroying this, you will receive the &6Machine Item",
                        "&7but, keep in mind, that you will lose all resources, you",
                        "&7had inside of it",
                        "",
                        "&eClick to destroy!"
                ).build(), (other) -> {
            new ConfirmMenu("&c&lDestroy machine", (destroy) -> {
                if (destroy){
                    brewingStand.destroy();
                    player.closeInventory();
                }
            }).openMenu(player);
        }));

        for (int i = 0; i < 27; i++) {
            if (buttons.containsKey(i)) continue;

            buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(7)
                    .name(" ")
                    .build()));
        }

        return buttons;
    }
}
