package cc.stormworth.hcf.profile.editor;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.menu.buttons.BackButton;
import cc.stormworth.hcf.misc.gkits.Kit;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.PlayerKit;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitManagementMenu extends Menu {
    private final Button PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");

    private final HCFProfile profile;
    private final Kit ladder;

    public KitManagementMenu(Kit ladder, HCFProfile profile) {
        this.ladder = ladder;
        this.profile = profile;

        profile.setSelectedKit(ladder);

        this.setPlaceholder(true);
        this.setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(final Player player) {
        return ChatColor.DARK_GRAY + "Viewing " + this.ladder.getName() + " kits";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        PlayerKit kit = profile.getKit(this.ladder);

        buttons.put(getSlot(4, 0), (kit == null) ? new CreateKitButton() : new KitDisplayButton(kit));
        buttons.put(getSlot(4, 1), (kit == null) ? PLACEHOLDER : new LoadKitButton());
        //buttons.put(getSlot(4, 2), (kit == null) ? PLACEHOLDER : new RenameKitButton(kit));
        buttons.put(getSlot(4, 2), (kit == null) ? PLACEHOLDER : new DeleteKitButton(kit));

        buttons.put(0, new BackButton(new SelectLadderKitMenu()));
        return buttons;
    }

    @Override
    public void onClose(final Player player) {
        if (!this.isClosedByMenu()) {
            profile.setSelectedKit(null);
        }
    }

    @RequiredArgsConstructor
    private class DeleteKitButton extends Button {
        private final PlayerKit kit;

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.WOOL)
                .name(ChatColor.RED.toString() + ChatColor.BOLD + "Delete")
                .data((short) 14)
                .addToLore(
                        "",
                        ChatColor.RED + "Click to delete this kit.",
                        ChatColor.RED + "You will " + ChatColor.BOLD + "NOT" + ChatColor.RED + " be able to",
                        ChatColor.RED + "recover this kit.")
                .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            profile.deleteKit(profile.getSelectedKit(), this.kit);
            setClosedByMenu(true);
            new KitManagementMenu(profile.getSelectedKit(), profile).openMenu(player);
        }
    }

    @RequiredArgsConstructor
    private class CreateKitButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Create Kit")
                .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            Kit ladder = profile.getSelectedKit();

            if (ladder == null) {
                player.closeInventory();
                return;
            }

            PlayerKit kit = new PlayerKit("Kit of " + ladder.getName(), ladder.getItems(), "Kit ");

            kit.setContents(profile.getSelectedKit().getItems());
            profile.replaceKit(profile.getSelectedKit(), kit);

            profile.setEditingKit(kit);
            setClosedByMenu(true);
            new KitEditorMenu(profile).openMenu(player);
        }
    }

    private class LoadKitButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.BOOK)
                .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Load/Edit")
                .addToLore("", ChatColor.YELLOW + "Click to edit this kit.")
                .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            if (profile.getSelectedKit() == null) {
                player.closeInventory();
                return;
            }

            PlayerKit kit = profile.getKit(profile.getSelectedKit());

            if (kit == null) {
                kit = new PlayerKit("Kit ", KitManagementMenu.this.ladder.getItems().clone(), "Kit ");

                player.setItemInHand(null);
                player.setItemOnCursor(null);

                kit.setContents(profile.getSelectedKit().getItems());
                profile.replaceKit(profile.getSelectedKit(), kit);
            }
            setClosedByMenu(true);
            profile.setEditingKit(kit);
            new KitEditorMenu(profile).openMenu(player);
        }
    }

    @RequiredArgsConstructor
    private class KitDisplayButton extends Button {
        private final PlayerKit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                .name(ChatColor.GREEN + ChatColor.BOLD.toString() + this.kit.getName())
                .build();
        }
    }
}
