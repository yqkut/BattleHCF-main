package cc.stormworth.hcf.team.menu.button;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.commands.ForceKickCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class KickPlayerButton extends Button {

    @NonNull
    private UUID uuid;
    @NonNull
    private Team team;

    @Override
    public String getName(Player player) {
        return "§cKick §e" + UUIDUtils.name(this.uuid);
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();

        if (team.isOwner(uuid)) {
            lore.add("§e§lLeader");
        } else if (team.isCoLeader(uuid)) {
            lore.add("§e§lCo-Leader");
        } else if (team.isCaptain(uuid)) {
            lore.add("§aCaptain");
        } else {
            lore.add("§7Member");
        }

        lore.add("");
        lore.add("§eClick to kick §b" + UUIDUtils.name(this.uuid) + "§e from team.");

        return lore;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        ForceKickCommand.forceKick(player, uuid);
    }
}