package cc.stormworth.hcf.profile.editor;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.RestoreInv;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitEditorMenu extends Menu {
    private final int[] ITEM_POSITIONS = new int[]{
            20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 47,
            48, 49, 50, 51, 52, 53
    };
    private final int[] BORDER_POSITIONS = new int[]{1, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 28, 37, 46};
    private final Button BORDER_BUTTON = Button.placeholder(Material.COAL_BLOCK, (byte) 0, " ");

    private final HCFProfile profile;

    public KitEditorMenu(HCFProfile profile) {
        this.profile = profile;
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(final Player player) {
        return ChatColor.AQUA + "Editing " + ChatColor.AQUA + profile.getEditingKit().getName();
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 54; i++) {
            buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).name(" ").build()));
        }

        for (final int border : BORDER_POSITIONS) {
            buttons.put(border, BORDER_BUTTON);
        }

        buttons.put(0, new CurrentKitButton());
        buttons.put(2, new SaveButton());
        buttons.put(6, new LoadDefaultKitButton());
        buttons.put(7, new ClearInventoryButton());
        buttons.put(8, new CancelButton());

        buttons.put(18, new ArmorDisplayButton(profile.getSelectedKit().getArmour()[3]));
        buttons.put(27, new ArmorDisplayButton(profile.getSelectedKit().getArmour()[2]));
        buttons.put(36, new ArmorDisplayButton(profile.getSelectedKit().getArmour()[1]));
        buttons.put(45, new ArmorDisplayButton(profile.getSelectedKit().getArmour()[0]));

        List<ItemStack> items = Arrays.asList(profile.getSelectedKit().getItems());

        for (int i = 20; i < Arrays.asList(profile.getSelectedKit().getItems()).size(); ++i) {
            ItemStack item = items.get(i - 20);
            if(item != null && item.getType() != Material.AIR){
                buttons.put(ITEM_POSITIONS[i - 20], new InfiniteItemButton(item));
            }
        }

        return buttons;
    }

    @Override
    public void onOpen(final Player player) {
        profile.setEditing(true);

        profile.setRestoreInv(new RestoreInv(player.getInventory().getArmorContents(), player.getInventory().getContents()));

        if (profile.getEditingKit() != null) {
            player.getInventory().setContents(profile.getEditingKit().getContents());
        }
        player.updateInventory();
    }

    @Override
    public void onClose(final Player player) {

        profile.setEditing(false);
        profile.setEditingKit(null);

        if (!isClosedByMenu()){
            profile.setSelectedKit(null);
        }

        if (profile.getRestoreInv() != null){
            player.getInventory().setArmorContents(profile.getRestoreInv().getArmor());
            player.getInventory().setContents(profile.getRestoreInv().getContent());

            player.updateInventory();

            profile.setRestoreInv(null);
        }

        /*if (!playerData.isInMatch()) {
            Bukkit.getScheduler()
                .scheduleSyncDelayedTask(
                    Practice.getInstance(),
                    () -> Practice.getInstance().getPlayerManager().reset(player),
                    1L);
        }*/
    }

    private class ArmorDisplayButton extends Button {
        private final ItemStack itemStack;

        @ConstructorProperties({"itemStack"})
        public ArmorDisplayButton(final ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public ItemStack getButtonItem(final Player player) {
            if (this.itemStack == null || this.itemStack.getType() == Material.AIR) {
                return new ItemStack(Material.AIR);
            }
            return new ItemBuilder(this.itemStack.clone())
                .addToLore("", ChatColor.GREEN + "This is automatically equipped.")
                .build();
        }
    }

    private class CurrentKitButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {

            return new ItemBuilder(Material.NAME_TAG)
                .name(ChatColor.AQUA
                        + ChatColor.BOLD.toString()
                        + "Editing: "
                        + ChatColor.GREEN
                        + profile.getEditingKit().getName())
                .build();
        }
    }

    private class ClearInventoryButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.WOOL)
                .data((short) 4)
                .name(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Clear Inventory")
                .addToLore(
                        "",
                            ChatColor.YELLOW + "This will clear your inventory",
                        ChatColor.YELLOW + "so you can start over.")
                .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType) {
            Button.playNeutral(player);
            player.setItemInHand(null);
            player.setItemOnCursor(null);
            player.getInventory().clear();
            player.updateInventory();
        }
    }

    private class LoadDefaultKitButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.WOOL)
                .data((short) 7)
                .name(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Load default kit")
                .addToLore(
                        "",
                            ChatColor.YELLOW + "Click this to load the default kit",
                            ChatColor.YELLOW + "into the kit editing menu."
                ).build();
        }

        @Override
        public void clicked(final Player player, final int i, final ClickType clickType) {
            Button.playNeutral(player);

            player.setItemInHand(null);
            player.setItemOnCursor(null);
            player.getInventory().setContents(profile.getSelectedKit().getItems());

            player.updateInventory();
        }
    }

    private class SaveButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.WOOL)
                .data((short) 5)
                .name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Save")
                .addToLore("", ChatColor.YELLOW + "Click this to save your kit.")
                .build();
        }

        @Override
        public void clicked(final Player player, final int i, final ClickType clickType) {
            Button.playNeutral(player);

            if (profile.getEditingKit() != null) {
                profile.getEditingKit().setContents(player.getInventory().getContents());
            }

            setClosedByMenu(true);

            new KitManagementMenu(profile.getSelectedKit(), profile).openMenu(player);
        }
    }

    private class CancelButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.WOOL)
                .data((short) 14)
                .name(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel")
                .addToLore(
                        "",
                        ChatColor.YELLOW + "Click this to abort editing your kit,",
                        ChatColor.YELLOW + "and return to the kit menu.")
                .build();
        }

        @Override
        public void clicked(final Player player, final int i, final ClickType clickType) {
            Button.playNeutral(player);

            if (profile.getSelectedKit() != null) {
                setClosedByMenu(true);
                new KitManagementMenu(profile.getSelectedKit(), profile).openMenu(player);
            }
        }
    }

    @RequiredArgsConstructor
    private class InfiniteItemButton extends Button {

        private final ItemStack itemStack;

        @Override
        public ItemStack getButtonItem(Player player) {
            return itemStack;
        }

        @Override
        public void clicked(final Player player, final int i, final ClickType clickType) {
            Inventory inventory = player.getOpenInventory().getTopInventory();

            if(inventory.getItem(i) == null){
                return;
            }

            ItemStack itemStack = inventory.getItem(i);
            inventory.setItem(i, itemStack);
            player.setItemOnCursor(itemStack);
            player.updateInventory();
        }
    }
}
