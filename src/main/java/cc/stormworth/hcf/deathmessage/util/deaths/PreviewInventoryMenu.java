package cc.stormworth.hcf.deathmessage.util.deaths;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.menu.menus.ConfirmMenu;
import cc.stormworth.core.uuid.MenuBackButton;
import cc.stormworth.hcf.commands.staff.DeathsCommand;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PreviewInventoryMenu extends Menu {

    private DeathInfo death;

    @Override
    public boolean isUpdateAfterClick() {
        return false;
    }

    @Override
    public String getTitle(Player player) {
        return CC.YELLOW + "Death: " + death.getId();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        ItemStack helmet = death.getArmor()[3];
        ItemStack chestplate = death.getArmor()[2];
        ItemStack leggings = death.getArmor()[1];
        ItemStack boots = death.getArmor()[0];
        if (helmet != null) {
            buttons.put(47, new Button() {
                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return helmet;
                }
            });
        }
        if (chestplate != null) {
            buttons.put(48, new Button() {
                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return chestplate;
                }
            });
        }
        if (leggings != null) {
            buttons.put(50, new Button() {
                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return leggings;
                }
            });
        }
        if (boots != null) {
            buttons.put(51, new Button() {
                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return boots;
                }
            });
        }

        int index = 0;
        if (death.getInventory() != null) {
            for (ItemStack item : death.getInventory()) {
                if (index >= 53) break;
                //if (inventoryUI.getItem(index) != null) index++;
                buttons.put(index, new Button() {
                    @Override
                    public String getName(Player player) {
                        return null;
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return null;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return null;
                    }

                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return item;
                    }
                });
                index++;
            }
        }

        for (int i = 0; i <= 53; i++) {
            if (buttons.get(i) == null) {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7));
            }
        }

        buttons.put(45, new MenuBackButton(p -> new DeathInfoMenu(death).openMenu(p)));
        buttons.put(53, new Button() {
            @Override
            public String getName(Player player) {
                return CC.YELLOW + "Refund this inventory";
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ENDER_CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ConfirmMenu("Refund " + death.getId(), data -> {
                    if (data) {
                        DeathsCommand.refund(player, death.getId());
                    } else {
                        player.sendMessage(CC.RED + "You did not confirm to refund the death.");
                    }
                }, true, new Button() {
                    @Override
                    public String getName(Player player) {
                        return CC.YELLOW + "Check Inventory";
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return null;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return Material.ENDER_CHEST;
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                        new PreviewInventoryMenu(death).openMenu(player);
                    }
                }).openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}