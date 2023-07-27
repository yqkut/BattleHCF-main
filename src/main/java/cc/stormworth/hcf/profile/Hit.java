package cc.stormworth.hcf.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class Hit {

  private final UUID uuid;
  private final ItemStack itemStack;
  private int hits;
}