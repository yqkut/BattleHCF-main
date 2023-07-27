package cc.stormworth.hcf.holograms;

import cc.stormworth.core.util.holograms.Hologram;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
@Setter
public class HoloNPC {

  private Location location;
  private Hologram hologram;

}