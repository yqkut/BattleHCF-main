package cc.stormworth.hcf.profile.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.listener.SetListener;
import cc.stormworth.hcf.misc.kits.KitManager;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.deathban.DeathBan;
import cc.stormworth.hcf.profile.pvptimer.PvPTimer;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.Utils;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import cc.stormworth.hcf.util.player.InventoryUtils;
import cc.stormworth.hcf.util.player.Players;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Date;
import java.util.UUID;

import static org.bukkit.Material.BOW;

@RequiredArgsConstructor
public class DeathBanListener implements Listener {

    private final Main plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerPreProcess(final PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getMapHandler().isKitMap() && HCFProfile.get(player).isDeathBanned() && !player.isOp() && !
                event.getMessage().startsWith("/msg")
                && !event.getMessage().startsWith("/tell")
                && !event.getMessage().startsWith("/report")
                && !event.getMessage().startsWith("/helpop")
                && !event.getMessage().startsWith("/request")
                && !event.getMessage().equalsIgnoreCase("/settings")
                && !event.getMessage().equalsIgnoreCase("/options")
                && !event.getMessage().startsWith("/deathban")
                && !event.getMessage().startsWith("/uselive")
                && !event.getMessage().startsWith("/hub")
                && !event.getMessage().startsWith("/lobby")) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot run commands while deathbanned!"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(final PlayerDropItemEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("void")) {

            HCFProfile profile = HCFProfile.get(event.getPlayer());

            if (profile.isDeathBanned()) {
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (plugin.getServerHandler().isEOTW()) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.getWorld().getName().equalsIgnoreCase("void")) {

                HCFProfile profile = HCFProfile.get(player);

                if (!profile.isDeathBanned()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
            return;
        }

        Player player = event.getPlayer();
        boolean shouldBypass = player.hasPermission("core.staff") || player.hasPermission("deathban.bypass");
        UUID playerUID = player.getUniqueId();

        HCFProfile profile = HCFProfile.get(player);

        if (profile == null){
            return;
        }

        if (!profile.isDeathBanned()){

            if (player.getWorld().getName().equalsIgnoreCase("void")) {

                player.getInventory().clear();
                player.getOpenInventory().getTopInventory().clear();
                player.getInventory().setArmorContents(null);

                player.updateInventory();
                player.teleport(plugin.getServerHandler().getSpawnLocation());
            }

            return;
        }

        plugin.getDeathbanRunnable().getOnlineDeathBannedPlayers().add(player.getUniqueId());

        if (shouldBypass && !plugin.getServerHandler().isEOTW() && !plugin.getServerHandler().isPreEOTW()) {
            profile.getDeathban().revive(playerUID);
            return;
        }

        /*if (plugin.getMapHandler().isKitMap() || !profile.isDeathBanned() ) {
            if (plugin.getRevivedMap().isRevived(player.getUniqueId())) {
                plugin.getDeathbanMap().revive(playerUID, true);
            }
            return;
        }*/

        long left = profile.getDeathban().getExpireAt() - System.currentTimeMillis();
        String time = TimeUtils.formatIntoDetailedString((int) left / 1000);

        /*if (shouldBypass && !plugin.getServerHandler().isEOTW() && !plugin.getServerHandler().isPreEOTW()) {
            plugin.getDeathbanMap().revive(playerUID, true);
            return;
        }*/

        Players.teleportWithChunkLoad(player, SetListener.getDeathban());

        event.getPlayer().sendMessage(CC.translate("&cYou have been sent to &lHospital."));
        event.getPlayer().sendMessage(CC.translate(""));
        event.getPlayer().sendMessage(CC.translate("&cClick the sign to revive yourself, if you have lives"));
        event.getPlayer().sendMessage(CC.translate("&cHere you can pvp against other deathbaned players!"));
        event.getPlayer().sendMessage(CC.translate(""));
        event.getPlayer().sendMessage(CC.translate("&cYou are still deathbanned for &l" + time));
        event.getPlayer().sendMessage(CC.translate("&cYou have &l" + profile.getLives() + " lives&c."));
        if (profile.getLives() == 0) {
            event.getPlayer().sendMessage(CC.translate("&cPurchase lives at &lstore.battle.rip."));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        HCFProfile profile = HCFProfile.get(player);
        if(profile == null) return;
        if(profile.isDeathBanned()){
            event.setRespawnLocation(SetListener.getDeathban());
            player.sendMessage(CC.RED + "You are death banned and cannot respawn.");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        //Main.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);

        Player player = event.getPlayer();

        HCFProfile profile = HCFProfile.get(player);
        if(profile == null) return;
        if(profile.isDeathBanned()){
            event.getPlayer().setExp(0.0F);
            event.getPlayer().setLevel(0);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        
        if(plugin.getTeamHandler().getTeam(player) != null && plugin.getFactionDuelManager().isInMatch(plugin.getTeamHandler().getTeam(player))){
            return;
        }

        if (plugin.getMapHandler().isKitMap()) {
            KitManager.getLastClicked().remove(player.getUniqueId());
        }

        Utils.removeThrownPearls(player);

        HCFProfile profile = HCFProfile.get(player);

        if (profile == null) {
            return;
        }

        if (player.getKiller() != null && profile.isDeathBanned()) {
            return;
        }

        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            // Add kills to sword lore if the victim does not equal the killer
            if (!event.getEntity().equals(killer)) {
                ItemStack hand = killer.getItemInHand();

                if (hand.getType().name().contains("SWORD") || hand.getType() == BOW) {
                    InventoryUtils.addKill(hand,
                            ChatColor.YELLOW + killer.getDisplayName() + ChatColor.YELLOW + " " + (hand.getType() == BOW ? "shot" : "killed") + " " + event.getEntity().getDisplayName());
                }

                double bal = profile.getEconomyData().getBalance();

                profile.getEconomyData().setBalance(0);

                if (bal > 0) {

                    HCFProfile profileKiller = HCFProfile.get(event.getEntity().getKiller());

                    profileKiller.getEconomyData().addBalance(bal);
                    event.getEntity().getKiller().sendMessage(ChatColor.GOLD + "You earned " + ChatColor.BOLD + "$" + bal + ChatColor.GOLD
                            + " for killing " + event.getEntity().getDisplayName() + ChatColor.GOLD
                            + "!");
                }
            }
        }

        Team playerTeam = plugin.getTeamHandler().getTeam(event.getEntity());

        if (playerTeam != null) {
            playerTeam.playerDeath(event.getEntity().getName(),
                    event.getEntity().getUniqueId(),
                    event.getEntity().getKiller() != null ? event.getEntity().getKiller() : null,
                    plugin.getServerHandler().getDTRLoss(event.getEntity()));
        }

        if (!EOTWCommand.isFfaEnabled()) {
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
        }

        CooldownAPI.removeCooldown(event.getEntity(), "enderpearl");

        boolean shouldBypass = event.getEntity().hasPermission("core.staff");

        if (plugin.getMapHandler().isKitMap() ||
                shouldBypass ||
                (event.getEntity().hasPermission("deathban.bypass") && !plugin.getServerHandler().isEOTW() && !plugin.getServerHandler().isPreEOTW())) {
            profile.setPvpTimer(new PvPTimer(false));
            return;
        }

        int seconds = (int) plugin.getServerHandler().getDeathban(event.getEntity());

        player.setFireTicks(0);

        if (!Main.getInstance().getEventHandler().getEclipseEvent().isActive()){
            profile.setDeathban(new DeathBan(seconds * 1000L));
            event.getEntity().setMetadata("deathban", new FixedMetadataValue(plugin, true));

            String time = TimeUtils.formatIntoDetailedString(seconds);

            if (!event.getEntity().isOnline()) {
                return;
            }

            if (plugin.getServerHandler().isPreEOTW() || plugin.getServerHandler().isEOTW()) {
                event.getEntity().kickPlayer(ChatColor.RED + "Thank you for playing! Come back next map!");
                return;
            }

            if (profile.hasPvPTimer()) {
                profile.setPvpTimer(null);
                //plugin.getPvPTimerMap().removeTimer(event.getEntity().getUniqueId());
            }

            //Players.teleportWithChunkLoad(event.getEntity(), SetListener.getDeathban());

            event.getEntity().sendMessage(CC.translate("&cYou have been sent to &lHospital."));
            event.getEntity().sendMessage(CC.translate(""));
            event.getEntity().sendMessage(CC.translate("&cClick the sign to revive yourself, if you have lives"));
            event.getEntity().sendMessage(CC.translate("&cHere you can pvp against other deathbaned players!"));
            event.getEntity().sendMessage(CC.translate(""));
            event.getEntity().sendMessage(CC.translate("&cYou are still deathbanned for &l" + time));
            event.getEntity().sendMessage(CC.translate("&cYou have &l" + profile.getLives() + " lives&c."));

            if (profile.getLives() == 0) {
                event.getEntity().sendMessage(CC.translate("&cPurchase lives at &lstore.battle.rip."));
            }
        }

        if (killer == null || !event.getEntity().equals(killer)) {
            String deathMsg = ChatColor.YELLOW + event.getEntity().getDisplayName() + ChatColor.YELLOW
                    + " " + (
                    (event.getEntity().getKiller() != null) ? ("killed by "
                            + event.getEntity().getKiller().getDisplayName()) : "died") + " " + ChatColor.GOLD
                    + InventoryUtils.DEATH_TIME_FORMAT.format(new Date());

            for (final ItemStack armor : event.getEntity().getInventory().getArmorContents()) {
                if (armor != null && armor.getType() != Material.AIR) {
                    InventoryUtils.addDeath(armor, deathMsg);
                }
            }
        }
    }

}
