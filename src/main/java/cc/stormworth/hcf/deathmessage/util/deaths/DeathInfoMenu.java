package cc.stormworth.hcf.deathmessage.util.deaths;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.menu.menus.ConfirmMenu;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.core.uuid.MenuBackButton;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.commands.staff.DeathsCommand;
import cc.stormworth.hcf.util.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DeathInfoMenu extends Menu {

    DeathInfo death;

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

        buttons.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return CC.GOLD + death.getDate();
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();

                lore.add(CC.SEPARATOR);
                lore.add(CC.translate("&eID: " + CC.RED + death.getId()));
                lore.add(CC.SEPARATOR);
                if (death.getKiller() != null)
                    lore.add(CC.translate("&eKiller: " + CC.RED + UUIDUtils.name(death.getKiller())));
                if (death.getTeam() != null) lore.add(CC.translate("&eTeam: " + CC.RED + death.getTeam()));
                if (death.getDtrInfo() != null) lore.add(CC.translate("&eDTR: " + CC.RED + death.getDtrInfo()));
                lore.add(CC.translate("&eMessage: " + CC.RED + death.getMessage()));
                lore.add(CC.SEPARATOR);

                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }
        });

        buttons.put(28, new Button() {
            @Override
            public String getName(Player player) {
                return CC.YELLOW + "Death Location";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();

                lore.add(CC.SEPARATOR);
                lore.add(CC.translate("&eWorld: " + CC.RED + death.getLocation().getWorld().getName()));
                lore.add(CC.translate("&eLocation: " + CC.RED + (int) death.getLocation().getX() + ", " + (int) death.getLocation().getY() + ", " + (int) death.getLocation().getZ()));
                lore.add(CC.SEPARATOR);
                lore.add(CC.translate("&eClick to teleport"));
                lore.add(CC.SEPARATOR);

                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.COMPASS;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.sendMessage(CC.YELLOW + "Teleporting...");
                player.teleport(death.getLocation());
            }
        });

        buttons.put(30, new Button() {
            @Override
            public String getName(Player player) {
                return CC.YELLOW + "Inventory";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();

                lore.add(CC.SEPARATOR);
                lore.add(CC.translate("&eRight Click to restore inventory"));
                lore.add(CC.translate("&eLeft Click to preview inventory"));
                lore.add(CC.SEPARATOR);

                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ENDER_CHEST;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().isBelow(Rank.MODPLUS)) {
                    player.sendMessage(CC.RED + "You don't have the sufficient permissions to refund inventories.");
                    this.playFail(player);
                    return;
                }
                if (clickType == ClickType.RIGHT) {
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
                } else if (clickType == ClickType.LEFT) {
                    new PreviewInventoryMenu(death).openMenu(player);
                }
            }
        });

        buttons.put(32, new Button() {
            @Override
            public String getName(Player player) {
                return CC.YELLOW + "Effects";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(CC.SEPARATOR);
                if (!Arrays.stream(death.getEffects()).collect(Collectors.toList()).isEmpty()) {
                    for (PotionEffect potionEffect : death.getEffects()) {
                        String time = potionEffect.getDuration() > 1_000_000 ? "Infinite" : TimeUtil.formatTime(potionEffect.getDuration() / 20, TimeUtil.FormatType.MILLIS_TO_MINUTES);
                        lore.add(CC.translate("&e" + Utils.convertFirstUpperCase(potionEffect.getType().getName().toLowerCase().replace("_", " ")) + " " + (potionEffect.getAmplifier() + 1) + ": &c" + time));
                    }
                } else {
                    lore.add("&cThere is no potion effects.");
                }
                lore.add(CC.SEPARATOR);

                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EXP_BOTTLE;
            }
        });

        buttons.put(34, new Button() {
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
                return (death.getTool() != null && death.getTool().getType() != Material.AIR) ? death.getTool() : ItemBuilder.of(Material.BEDROCK).name(CC.YELLOW + "No killer tool").build();
            }
        });

        buttons.put(40, new MenuBackButton(p -> new DeathsMenu(death.getVictim()).openMenu(p)));

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 45;
    }
}