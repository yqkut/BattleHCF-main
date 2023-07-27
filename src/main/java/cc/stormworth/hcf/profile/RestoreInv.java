package cc.stormworth.hcf.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public class RestoreInv {

    private final ItemStack[] armor;
    private final ItemStack[] content;

}
