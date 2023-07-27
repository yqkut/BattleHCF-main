package cc.stormworth.hcf.team.menu.button;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.commands.TeamManageCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OpenMuteMenuButton extends Button {
    private final Team team;

    public OpenMuteMenuButton(final Team team) {
        this.team = team;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder.of(getMaterial(player)).name(getName(player)).setLore(getDescription(player)).data(getDamageValue(player)).build();
    }

    public void clicked(Player player, int slot, ClickType clickType) {
        TeamManageCommand.muteTeam(player, this.team);
    }

    public String getName(final Player player) {
        return "§7Mute Team";
    }

    public List<String> getDescription(final Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(final Player player) {
        return 0;
    }

    public Material getMaterial(final Player player) {
        return Material.CHEST;
    }
}
