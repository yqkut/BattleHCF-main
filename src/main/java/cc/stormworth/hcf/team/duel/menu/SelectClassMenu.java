package cc.stormworth.hcf.team.duel.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class SelectClassMenu extends Menu {

    private final Team team;
    private final Team otherTeam;
    private final Player target;
    private boolean isDueling;

    @Override
    public String getTitle(Player player) {
        return "&6&lSelect class";
    }

    private String getSelectedClass(Player player) {

        String pvpClass = team.getPvpClassesMap().get(player.getUniqueId());

        if (pvpClass.equalsIgnoreCase("Bard")) {
            return "&6Bard";
        } else if (pvpClass.equalsIgnoreCase("Archer")) {
            return "&5Archer";
        } else if (pvpClass.equalsIgnoreCase("Rouge")) {
            return "&7Rogue";
        } else {
            return "&bDiamond";
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
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

        buttons.put(getSlot(0, 1), Button.fromItem(glass.data(yellowData).build()));

        buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

        for (int i = 0; i < 9; i++) {
            buttons.put(getSlot(i, 2), Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
        }

        buttons.put(getSlot(4, 0), Button.fromItem(new ItemBuilder(Material.SKULL_ITEM)
                        .data((short) 3)
                .name(target.getDisplayName() + "'s Class selection").setSkullOwner(target.getName()).build()));

        buttons.put(getSlot(3, 0), Button.fromItem(new ItemBuilder(Material.BED)
                .name("&cGo back")
                .addToLore(
                    "",
                    "&7You are currently editing &f" + target.getName() + "&7 member class.",
                    "",
                    "&7- (Currently " + getSelectedClass(target) + " &7selected)",
                    ""
                ).build(), (other) -> new FactionDuelMenu(team, otherTeam, isDueling).open(other)));



        buttons.put(getSlot(5, 0), Button.fromItem(new ItemBuilder(Material.FLINT_AND_STEEL)
                .name("&cReset " + player.getName() + "'s Class")
                        .addToLore("&7Click to reset " + player.getName() + "'s class")
                .build(), other-> {
                    team.getPvpClassesMap().put(target.getUniqueId(), "Diamond");
                    player.sendMessage(CC.translate("&cYou have reset " + target.getName() + "'s class to Diamond"));
        }));

        buttons.put(getSlot(1, 1), new ClassButton("Diamond", ChatColor.AQUA, Material.DIAMOND_HELMET));
        buttons.put(getSlot(3, 1), new ClassButton("Bard", ChatColor.GOLD, Material.GOLD_HELMET));
        buttons.put(getSlot(5, 1), new ClassButton("Rogue", ChatColor.GRAY, Material.CHAINMAIL_HELMET));
        buttons.put(getSlot(7, 1), new ClassButton("Archer", ChatColor.LIGHT_PURPLE, Material.LEATHER_HELMET));

        return buttons;
    }

    @AllArgsConstructor
    public class ClassButton extends Button {

        private String className;
        private ChatColor chatColor;
        private Material material;

        @Override
        public String getName(Player player) {
            return chatColor + ChatColor.BOLD.toString() + className + " Class";
        }

        @Override
        public List<String> getDescription(Player player) {
            return CC.translate(Lists.newArrayList(
                    "",
                    chatColor + "Click to " +
                            (team.getPvpClassesMap().get(target.getUniqueId()).equalsIgnoreCase(className) ?
                                    "select" : "deselect") + "!"
            ));
        }

        @Override
        public Material getMaterial(Player player) {
            if (team.getPvpClassesMap().containsValue(className)) {
                return Material.REDSTONE_BLOCK;
            }

            return material;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(super.getButtonItem(player)).setGlowing(true).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

            team.getPvpClassesMap().put(target.getUniqueId(), className);

            player.sendMessage(CC.translate("&eYou have selected &6" + className + " &eclass for &6" + target.getName() + "&e!"));
        }
    }
}
