package cc.stormworth.hcf.team.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

@Setter
@Getter
@AllArgsConstructor
public class RegenBlock {

    private Location location;
    private Material material;
    private byte data;
}