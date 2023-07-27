package cc.stormworth.hcf.team.menu.base;

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
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class CreateBaseMenu extends Menu {

    private final Team team;

    @Setter private int color = -1;
    @Setter private int height = 8;

    @Override
    public String getTitle(Player player) {
        return "&eCreate Base";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 3 * 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();

        boolean finished = color != 0 && height != 0;

        if (finished){
            buttons.put(getSlot(4, 0), Button.fromItem(new ItemBuilder(
                    Material.EMERALD)
                    .name("&a&lSetup Finished.")
                    .addToLore(
                            "&6➞ &eStatus&7:",
                            "&6► &7Color: &7(" + (getColorName(color)) + "&7)",
                            "&6► &7Height: &7(&9" + height + " blocks&7)",
                            "",
                            "&eClick to continue."
                    )
                    .build(), other -> {

                player.closeInventory();
                VisualClaim visualClaim = new VisualClaim(player, team, VisualClaimType.BASE, false, true);

                visualClaim.setHeightAmount(height);
                visualClaim.setColorGlass(color);

                visualClaim.draw(false);

                other.sendMessage(ChatColor.GREEN + "Gave you a select wand.");
            }));
        }else{
            buttons.put(getSlot(4, 0), Button.fromItem(new ItemBuilder(Material.INK_SACK)
                    .setDurability(1)
                    .name("&c&lSetup Not Finished.")
                    .addToLore(
                            "&6➞ &eStatus",
                            (color == -1 ? "&6► &cColor: &7(&cPending&7)" : "&6► &aColor: &7(Updated&7)"),
                            (height <= 8 ? "&6► &cHeight: &7(&cPending)" : "&6► &aHeight: &7(Updated)"),
                            "",
                            color == -1 ? "&eClick to update color." : height <= 8 ? "&eClick to update height." : "&eClick to continue."
                    )
                    .build(), other -> {
                if (color == -1){
                    new SelectBaseColorMenu(CreateBaseMenu.this).openMenu(other);
                }else if (height == 0){
                    new SelectHeightMenu(CreateBaseMenu.this).openMenu(other);
                }
            }));
        }


        buttons.put(getSlot(2, 1), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.WOOL)
                        .setWoolColour(DyeColor.ORANGE)
                        .name("&6Color &7(Settings)")
                        .addToLore(
                                "",
                                "&6➞ &eSelected: " + (getColorName(color) == null ? "&c&lNot Set" : getColorName(color)),
                                "",
                                "&eClick to update."
                        ).build();
            }

            @Override
            public void clicked(InventoryClickEvent event) {
                new SelectBaseColorMenu(CreateBaseMenu.this).openMenu((Player) event.getWhoClicked());
            }
        });

        buttons.put(getSlot(6, 1), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.WOOL)
                        .setWoolColour(DyeColor.LIME)
                        .name("&aHeight &7(Settings)")
                        .addToLore(
                                "",
                                "&6➞ &eSelected: " + (height == 0 ? "&c&lNot Set" : height),
                                "",
                                "&eClick to update."
                        ).build();
            }

            @Override
            public void clicked(InventoryClickEvent event) {
                new SelectHeightMenu(CreateBaseMenu.this).openMenu((Player) event.getWhoClicked());
            }
        });


        for (int i = 0; i < 27; i++) {
            if (buttons.containsKey(i)) continue;

            buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(7)
                    .name(" ")
                    .build()));
        }

        return buttons;
    }

    public String getColorName(int color){
        switch(color){
            case 0:
                return ChatColor.WHITE + "White";
            case 1:
                return ChatColor.GOLD + "Orange";
            case 2:
                return ChatColor.DARK_PURPLE + "Magenta";
            case 3:
                return ChatColor.AQUA + "Light Blue";
            case 4:
                return ChatColor.YELLOW + "Yellow";
            case 5:
                return ChatColor.GREEN + "Lime";
            case 6:
                return ChatColor.LIGHT_PURPLE + "Pink";
            case 7:
                return ChatColor.DARK_GRAY + "Gray";
            case 8:
                return ChatColor.GRAY + "Light Gray";
            case 9:
                return ChatColor.BLUE + "Cyan";
            case 10:
                return ChatColor.DARK_PURPLE + "Purple";
            case 11:
                return ChatColor.DARK_BLUE + "Blue";
            case 12:
                return ChatColor.GOLD + "Brown";
            case 13:
                return ChatColor.DARK_GREEN + "Green";
            case 14:
                return ChatColor.RED + "Red";
            case 15:
                return ChatColor.BLACK + "Black";
            default:
                return null;
        }
    }
}
