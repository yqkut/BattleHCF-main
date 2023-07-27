package cc.stormworth.hcf.team.menu.button;

import cc.stormworth.core.menu.Button;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.commands.ForceDisbandCommand;
import cc.stormworth.hcf.team.menu.ConfirmMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DisbandTeamButton extends Button {

    private Team team;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        new ConfirmMenu("Disband?", (b) -> {
            if (b) {
                ForceDisbandCommand.forceDisband(player, team);
            }
        }).openMenu(player);
    }

    @Override
    public String getName(Player player) {
        return "§c§lDisband Team";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.TNT;
    }
}