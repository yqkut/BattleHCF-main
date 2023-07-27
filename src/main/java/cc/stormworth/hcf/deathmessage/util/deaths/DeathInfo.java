package cc.stormworth.hcf.deathmessage.util.deaths;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeathInfo {

    private String id;
    private UUID victim;
    private UUID killer;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private PotionEffect[] effects;
    private Location location;
    private ItemStack tool;
    private String message;
    private String team;
    private String dtrInfo;
    private String date;
}