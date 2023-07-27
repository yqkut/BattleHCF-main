package cc.stormworth.hcf.team.menu.falltrap;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.claims.VisualClaimType;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
@Getter @Setter
public class FalltrapMenu extends Menu {

    private Material selectedMaterial;
    private int selectedData = 0;

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "Select FallTrap Walls blocks";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(getSlot(4, 0), Button.fromItem(new ItemBuilder(
                isFinished() ? Material.EMERALD : Material.INK_SACK)
                .setDurability(isFinished() ? 0 : 1)
                .name(isFinished() ? "&a&lSetup Finished." : "&c&lSetup Not Finished.")
                .addToLore(
                        "&eStatus",
                        "&7Material: "
                                + (selectedMaterial == null ? "&c&lNot Set" : StringUtils.capitalize(selectedMaterial.name().toLowerCase().replace("_", " ")))
                ).build(), other -> {

            if (isFinished()) {

                player.closeInventory();
                VisualClaim visualClaim = new VisualClaim(player, team, VisualClaimType.FALLTRAP, false, true);

                visualClaim.setMaterial(selectedMaterial);
                visualClaim.setColorGlass(selectedData);

                visualClaim.draw(false);

                other.sendMessage(ChatColor.GREEN + "Gave you a select wand.");
            }
        }));


        buttons.put(getSlot(2, 1), Button.fromItem(new ItemBuilder(Material.WORKBENCH).name("&eCrafting Table").addToLore(
                "",
                selectedMaterial == Material.WORKBENCH ? "&aSelected" : "&eSelect Crafting table as wall")
                .build(), other -> setSelectedMaterial(Material.WORKBENCH)));

        buttons.put(getSlot(4, 1), Button.fromItem(new ItemBuilder(Material.CHEST).name("&eEmpty").addToLore(
                "", selectedMaterial == Material.CHEST ? "&aSelected" : "&eSelect Empty chest as wall")
                .build(), other -> setSelectedMaterial(Material.AIR)));

        buttons.put(getSlot(6, 1), Button.fromItem(new ItemBuilder(Material.FURNACE).name("&eFurnace").addToLore(
                        "",
                        selectedMaterial == Material.FURNACE ? "&aSelected" : "&eSelect furnace as wall")
                .build(), other -> setSelectedMaterial(Material.FURNACE)));


        buttons.put(getSlot(4, 2), Button.fromItem(new ItemBuilder(Material.WOOL).name("&eWool")
                .addToLore("", "&eSelect wool as wall")
                .build(), other -> new SelectWallColorMenu(this).openMenu(player)));

        for (int i = 0; i < 36; i++) {
            if (buttons.containsKey(i)) continue;

            buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(7)
                    .name(" ")
                    .build()));
        }

        return buttons;
    }

    private boolean isFinished() {
        return selectedMaterial != null;
    }
}
