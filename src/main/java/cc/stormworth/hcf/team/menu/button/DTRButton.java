package cc.stormworth.hcf.team.menu.button;

import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DTRButton extends Button {
    Team team;
    boolean increase;

    public DTRButton(final Team team, final boolean increase) {
        this.team = team;
        this.increase = increase;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder.of(getMaterial(player)).name(getName(player)).setLore(getDescription(player)).data(getDamageValue(player)).build();
    }

    public void clicked(Player player, int slot, ClickType clickType) {
        if (!this.increase && this.team.getDTR() - 1.0 <= 0.0 && !player.hasPermission("hcf.dtr.setraidable")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set teams as raidable. This has been logged.");
            return;
        }
        if (this.increase && this.team.getMaxDTR() <= this.team.getDTR() + 1.0) {
            player.sendMessage(ChatColor.RED + "This would put the team above their maximum DTR. This has been logged.");
            return;
        }
        if (this.increase) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 0.1f);
            this.team.setDTR(this.team.getDTR() + 1.0);
        } else {
            this.team.setDTR(this.team.getDTR() - 1.0);
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0f, 0.1f);
        }
        player.closeInventory();
    }

    public String getName(final Player player) {
        return this.increase ? "§aIncrease by 1.0" : "§cDecrease by 1.0";
    }

    public List<String> getDescription(final Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(final Player player) {
        return (byte) (this.increase ? 5 : 14);
    }

    public Material getMaterial(final Player player) {
        return Material.WOOL;
    }
}