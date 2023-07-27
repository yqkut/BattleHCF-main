package cc.stormworth.hcf.profile.editor;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.gkits.Kit;
import cc.stormworth.hcf.profile.HCFProfile;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SelectLadderKitMenu extends Menu {

    @Override
    public String getTitle(final Player player) {
        return ChatColor.RED + ChatColor.BOLD.toString() + "Select a kit...";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Kit kit : Main.getInstance().getKitManager().getKits()) {
            if (kit.canUse(player)){
                buttons.put(buttons.size(), new LadderKitDisplayButton(kit));
            }
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private class LadderKitDisplayButton extends Button {
        private final Kit ladder;

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(this.ladder.getImage().getType())
                .name(ChatColor.GREEN + ChatColor.BOLD.toString() + this.ladder.getName())
                .addToLore(
                        "",
                        ChatColor.WHITE
                            + "Click to select "
                            + ChatColor.RED
                            + ladder.getName()
                            + ChatColor.WHITE
                            + ".")
                .data(this.ladder.getImage().getDurability())
                .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();

            HCFProfile profile = HCFProfile.get(player);
            profile.setSelectedKit(ladder);

            new KitManagementMenu(ladder, profile).openMenu(player);
        }
    }
}
