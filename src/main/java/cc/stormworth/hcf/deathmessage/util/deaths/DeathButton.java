package cc.stormworth.hcf.deathmessage.util.deaths;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class DeathButton extends Button {

    private DeathInfo death;

    @Override
    public String getName(Player player) {
        return CC.GOLD + death.getDate();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();

        lore.add(CC.SEPARATOR);
        lore.add(CC.translate("&eID: " + CC.RED + death.getId()));
        if (death.getKiller() != null)
            lore.add(CC.translate("&eKiller: " + CC.RED + UUIDUtils.name(death.getKiller())));
        if (death.getTeam() != null) lore.add(CC.translate("&eTeam: " + CC.RED + death.getTeam()));
        if (death.getDtrInfo() != null) lore.add(CC.translate("&eDTR: " + CC.RED + death.getDtrInfo()));
        lore.add(CC.translate("&eMessage: " + CC.RED + death.getMessage()));
        lore.add(CC.SEPARATOR);
        lore.add(CC.translate("&eClick to manage this death"));
        lore.add(CC.SEPARATOR);

        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new DeathInfoMenu(death).openMenu(player);
    }
}