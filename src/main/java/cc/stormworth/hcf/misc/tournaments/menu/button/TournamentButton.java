package cc.stormworth.hcf.misc.tournaments.menu.button;

import cc.stormworth.core.menu.Button;
import cc.stormworth.hcf.misc.tournaments.TournamentType;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class TournamentButton extends Button {

    private TournamentType type;

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Click to host " + type.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return Lists.newArrayList();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {

    }
}

