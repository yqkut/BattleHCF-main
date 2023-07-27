package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.util.misc.SetupStage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class SetupListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (SetupStage.getStage() == SetupStage.NONE) return;
        Player player = event.getPlayer();

        if (!player.isOp()) return;
        SetupStage.giveItem(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (SetupStage.getStage() == SetupStage.NONE) return;
        Player player = event.getPlayer();

        if (!player.isOp() || !event.getAction().name().startsWith("RIGHT_")) return;
        ItemStack itemStack = event.getItem();
        if (itemStack == SetupStage.getBackbutton()) {
            SetupStage stage = SetupStage.values()[SetupStage.getStage().getId() - 1];
            if (stage != SetupStage.NONE) SetupStage.setStage(stage);
            event.setCancelled(true);
            return;
        }
        if (itemStack == SetupStage.getStage().getItemStack()) {
            player.performCommand(SetupStage.getStage().getCommand());

            SetupStage stage = SetupStage.values()[SetupStage.getStage().getId() + 1];
            if (stage == null) {
                SetupStage.setStage(SetupStage.NONE);
                Bukkit.broadcastMessage(CC.GREEN + "The setup mode has ended.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reboot 10s");
            } else {
                if (stage != SetupStage.NONE) SetupStage.setStage(stage);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (SetupStage.getStage() == SetupStage.NONE) return;
        Player player = (Player) event.getWhoClicked();

        if (!player.isOp()) return;
        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(SetupStage.getStage().getItemStack()))
            event.setCancelled(true);
    }
}