package cc.stormworth.hcf.misc.war.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.FactionWarManager;
import cc.stormworth.hcf.misc.war.FactionWarParticipant;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.pvpclasses.pvpclasses.ArcherClass;
import cc.stormworth.hcf.pvpclasses.pvpclasses.BardClass;
import cc.stormworth.hcf.pvpclasses.pvpclasses.RogueClass;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public final class FactionWarJoinMenu extends PaginatedMenu {

    private final Team team;
    private final FactionWar war;

    private final Map<UUID, PvPClass> pvpClassesMap = Maps.newHashMap();

    {
        setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&6&lSet members classes";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Player member : this.team.getOnlineMembers()) {
            buttons.put(buttons.size(), new MemberButton(member));
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return ImmutableMap.of(4, new ContinueButton());
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 54;
    }

    private int countBards() {
        return (int) this.pvpClassesMap.values().stream().filter(pvpClass -> pvpClass instanceof BardClass).count();
    }

    private int countArchers() {
        return (int) this.pvpClassesMap.values().stream().filter(pvpClass -> pvpClass instanceof ArcherClass).count();
    }

    private int countRogues() {
        return (int) this.pvpClassesMap.values().stream().filter(pvpClass -> pvpClass instanceof RogueClass).count();
    }

    private PvPClass getNextClass(UUID uuid) {
        PvPClassHandler pvpClassManager = Main.getInstance().getPvpClassHandler();

        if (!this.pvpClassesMap.containsKey(uuid)) {
            return pvpClassManager.getPvpClazz(BardClass.class);
        } else {
            PvPClass currentClass = this.pvpClassesMap.get(uuid);

            if (currentClass instanceof BardClass) {
                return pvpClassManager.getPvpClazz(ArcherClass.class);
            } else if (currentClass instanceof ArcherClass) {
                return pvpClassManager.getPvpClazz(BardClass.class);
            } else {
                return null;
            }
        }
    }

    @AllArgsConstructor
    final class MemberButton extends Button {

        private final Player member;

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
            String memberName = this.member.getName();
            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM).data((short) 3).name("&6" + memberName).setSkullOwner(memberName);

            boolean isDiamond = pvpClassesMap.containsKey(this.member.getUniqueId());
            boolean isBard = !isDiamond && pvpClassesMap.get(this.member.getUniqueId()) instanceof BardClass;
            boolean isArcher = !isDiamond && pvpClassesMap.get(this.member.getUniqueId()) instanceof BardClass;
            boolean isRogue = !isDiamond && pvpClassesMap.get(this.member.getUniqueId()) instanceof BardClass;

            List<String> lore = Lists.newArrayList();

            lore.add("");
            lore.add("&7Current class:");
            lore.add("&f- " + (isDiamond ? "&aDiamond" : "&cDiamond"));
            lore.add("&f- " + (isBard ? "&aBard" : "&cBard"));
            lore.add("&f- " + (isArcher ? "&aArcher" : "&cArcher"));
            lore.add("&f- " + (isRogue ? "&aRogue" : "&cRogue"));
            lore.add("");
            lore.add("&eClick to switch " + this.member.getName() + "'s class.");

            return item.setLore(lore).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            UUID memberUUID = this.member.getUniqueId();
            PvPClass nextClass = getNextClass(memberUUID);

            if (nextClass == null) {
                pvpClassesMap.remove(memberUUID);

                playNeutral(player);
                return;
            } else {
                if (nextClass instanceof BardClass && countBards() < FactionWarManager.MAX_BARDS) {
                    pvpClassesMap.put(memberUUID, nextClass);

                    playNeutral(player);
                    return;
                } else if (nextClass instanceof ArcherClass && countArchers() < FactionWarManager.MAX_ARCHERS) {
                    pvpClassesMap.put(memberUUID, nextClass);

                    playNeutral(player);
                    return;
                } else if (nextClass instanceof RogueClass && countRogues() < FactionWarManager.MAX_ROGUES) {
                    pvpClassesMap.put(memberUUID, nextClass);

                    playNeutral(player);
                    return;
                }

                playFail(player);

                player.sendMessage(CC.translate("&cYou can't set this PvP class to " + this.member.getName() + " because your faction has reached the limit of it!"));
            }
        }

    }

    final class ContinueButton extends Button {

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
            return new ItemBuilder(Material.WOOL)
                    .data((short) 5)
                    .name("&a&lContinue")
                    .addToLore(
                            "",
                            "&7Once you have set all your faction members",
                            "&7pvp classes, click this button to join",
                            "&7the faction war.",
                            "",
                            "&eClick to continue and join the faction war!"
                    )
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (war == null) {
                return;
            }

            if (war.getState() != FactionWar.FactionWarState.STARTING) {
                playFail(player);

                player.sendMessage(CC.translate("&cThe faction war has already started!"));
                return;
            }

            if (war.isFull()) {
                playFail(player);

                player.sendMessage(CC.translate("&cThe faction war is full!"));
                return;
            }

            if (team.getOnlineMembers().size() < team.getMembers().size()) {
                playFail(player);

                player.sendMessage(CC.translate("&cAll members of your faction must be online in order to join the faction war!"));
                return;
            }

            war.addParticipant(new FactionWarParticipant(team, pvpClassesMap.entrySet()));

            playSuccess(player);

            player.closeInventory();

            team.sendMessage(CC.translate("&eYour faction has &asuccessfully &ejoined the faction war."));
        }
    }
}
