package cc.stormworth.hcf.team.menu.button;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.commands.ForceLeaderCommand;
import cc.stormworth.hcf.team.menu.ConfirmMenu;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MakeLeaderButton extends Button {

    @NonNull
    private UUID uuid;
    @NonNull
    private Team team;

    @Override
    public String getName(Player player) {
        return (team.isOwner(uuid) ? "§a§l" : "§7") + UUIDUtils.name(this.uuid);
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();

        if (team.isOwner(uuid)) {
            lore.add("§aThis player is already the leader!");
        } else {
            lore.add("§eClick to change §b" + team + "§b's§e leader");
            lore.add("§eto §6" + UUIDUtils.name(this.uuid));
        }

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
        if (team.isOwner(uuid)) {
            player.sendMessage(ChatColor.RED + "That player is already the leader!");
            return;
        }

        new ConfirmMenu("Make " + UUIDUtils.name(this.uuid) + " leader?", (b) -> {
            if (b) {
                ForceLeaderCommand.forceLeader(player, uuid);

            }
        }).openMenu(player);
    }
}