package cc.stormworth.hcf.team.utils;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

public class FilterMenu extends Menu {

    public String getTitle(final Player player) {
        return CC.GOLD + "Faction Filter Menu";
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public boolean isUpdateAfterClick() {
        return false;
    }

    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        short orangeData = 1;
        short yellowData = 4;

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
                .name(" ")
                .setGlowing(true);

        for (int i = 0; i < 9; i++) {
            buttons.put(i,
                    Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));

        buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

        for (int i = 0; i < 9; i++) {
            buttons.put(getSlot(i, 2),
                    Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GOLD + "Options";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> description = Lists.newArrayList();
                HCFProfile hcfProfile = HCFProfile.getByUUIDIfAvailable(player.getUniqueId());
                FilterType type = hcfProfile == null ? FilterType.HIGHEST_ONLINE : hcfProfile.getFilterType();
                description.add("");
                description.addAll(ImmutableList.of(
                        ChatColor.GOLD + "Select a filter to show",
                        ChatColor.GOLD + "in the /f list command"
                ));
                description.add("");
                if (type == FilterType.HIGHEST_ONLINE) {
                    description.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "  • " + ChatColor.YELLOW + "Teams with the highest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest DTR");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest DTR");
                } else if (type == FilterType.LOWEST_ONLINE) {
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest online members");
                    description.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "  • " + ChatColor.YELLOW + "Teams with the lowest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest DTR");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest DTR");
                } else if (type == FilterType.HIGHEST_DTR) {
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest online members");
                    description.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "  • " + ChatColor.YELLOW + "Teams with the highest DTR");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest DTR");
                } else if (type == FilterType.LOWEST_DTR) {
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest DTR");
                    description.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "  • " + ChatColor.YELLOW + "Teams with the lowest DTR");
                } else {
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest online members");
                    description.add("    " + ChatColor.YELLOW + "Teams with the highest DTR");
                    description.add("    " + ChatColor.YELLOW + "Teams with the lowest DTR");
                }
                return description;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ITEM_FRAME;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                HCFProfile hcfProfile = HCFProfile.getByUUIDIfAvailable(player.getUniqueId());
                if (hcfProfile == null) return;
                FilterType type = hcfProfile.getFilterType();
                if (type == FilterType.HIGHEST_ONLINE) {
                    hcfProfile.setFilterType(FilterType.LOWEST_ONLINE);
                } else if (type == FilterType.LOWEST_ONLINE) {
                    hcfProfile.setFilterType(FilterType.HIGHEST_DTR);
                } else if (type == FilterType.HIGHEST_DTR) {
                    hcfProfile.setFilterType(FilterType.LOWEST_DTR);
                } else if (type == FilterType.LOWEST_DTR) {
                    hcfProfile.setFilterType(FilterType.HIGHEST_ONLINE);
                } else {
                    hcfProfile.setFilterType(FilterType.HIGHEST_ONLINE);
                }
            }
        });
        return buttons;
    }
}