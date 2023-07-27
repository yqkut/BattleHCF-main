package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class AntiFallTrap extends InteractAbility {

    public AntiFallTrap() {
        super("AntiFallTrap",
                "&bAnti FallTrap",
                Lists.newArrayList(
                        "",
                        "&7Prevents you from taking fall damage",
                        ""
                ),
                new ItemStack(Material.FEATHER),
                TimeUtil.parseTimeLong("1m"));
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            if (block.getType().name().contains("SIGN") || block.getType().name().contains("WALL_SIGN")) {
                Sign sign = (Sign) block.getState();
                if (sign.getLine(0).equalsIgnoreCase(CC.translate("&9[Elevator]"))) {
                    return;
                }
            }
        }

        player.setMetadata("anti_falltrap", new FixedMetadataValue(Main.getInstance(), true));

        super.onInteract(event);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (player.hasMetadata("anti_falltrap")) {
                    event.setCancelled(true);
                    player.removeMetadata("anti_falltrap", Main.getInstance());
                }
            }
        }
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return null;
    }
}
