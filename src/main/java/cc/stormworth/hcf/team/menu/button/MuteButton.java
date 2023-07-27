package cc.stormworth.hcf.team.menu.button;

import cc.stormworth.core.menu.Button;
import cc.stormworth.hcf.team.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MuteButton extends Button {
    private final int minutes;
    private final Team team;

    public MuteButton(final int minutes, final Team team) {
        this.minutes = minutes;
        this.team = team;
    }

    public void clicked(Player player, int slot, ClickType clickType) {
        player.performCommand("team mute " + team.getName() + " " + minutes + "m " + " Team Mute");
    }

    public String getName(final Player player) {
        return "§e" + this.minutes + "m mute";
    }

    public List<String> getDescription(final Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(final Player player) {
        return 0;
    }

    public ItemStack getButtonItem(final Player player) {
        final ItemStack it = new ItemStack(this.getMaterial(player));
        final ItemMeta im = it.getItemMeta();
        im.setLore(this.getDescription(player));
        im.setDisplayName(this.getName(player));
        it.setItemMeta(im);
        it.setAmount(this.minutes);
        return it;
    }

    public Material getMaterial(final Player player) {
        return Material.CHEST;
    }
}