package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;
import static org.bukkit.Material.POTION;

public class NonKitMapListener implements Listener {

  public NonKitMapListener() {
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
  }

    /*@EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        if (damager == null || victim == damager) return;
        if (Utils.isEventLocated(victim, true)) {
            Team victimTeam = Main.getInstance().getTeamHandler().getTeam(victim);
            if (victimTeam == null || !Main.getInstance().getEventHandler().getJoinedTeams().contains(victimTeam)) {
                damager.sendMessage(CC.RED + "You cannot attack players that are not in the event.");
                event.setCancelled(true);
            }

            if (victimTeam == null || (victimTeam != null && Main.getInstance().getEventHandler().getJoinedTeams().contains(victimTeam))) {
                Team team = Main.getInstance().getTeamHandler().getTeam(damager);
                if (team == null || !Main.getInstance().getEventHandler().getJoinedTeams().contains(team)) {
                    damager.sendMessage(CC.RED + "You cannot attack players that are in the event.");
                    event.setCancelled(true);
                }
            }
        }
    }*/

  /*@EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();

    if (!Utils.isDeathbanned(player.getUniqueId())) {
      return;
    }

    new BukkitRunnable() {
      public void run() {
        if (!player.isOnline()) {
          return;
        }
        Players.teleportWithChunkLoad(player, SetListener.getDeathban());
      }
    }.runTaskLater(Main.getInstance(), 10L);
  }*/

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (CustomTimerCreateCommand.isSOTWTimer()) {
      return;
    }
    final Player player = event.getPlayer();
    if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR
        || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      if (event.getItem().getType() == POTION) {
        try { // Ensure that any errors with Potion.fromItemStack don't mess with the rest of the code.
          ItemStack i = event.getItem();

          // We can't run Potion.fromItemStack on a water bottle.
          if (i.getDurability() != (short) 0) {
            Potion pot = Potion.fromItemStack(i);

            if (pot != null && pot.isSplash() && pot.getType() != null
                && PlayerListener.DEBUFFS.contains(pot.getType().getEffectType())) {
              if (!Main.getInstance().getMapHandler().isKitMap() && HCFProfile.get(player).hasPvPTimer()) {
                player.sendMessage(RED + "You cannot do this while your PVP Timer is active!");
                player.sendMessage(RED + "Type '" + YELLOW + "/pvp enable" + RED + "' to remove your timer.");
                event.setCancelled(true);
                return;
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    if (event.getClickedBlock() == null) {
      return;
    }
    if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE
        && event.getAction() == Action.LEFT_CLICK_BLOCK) {
      if (event.getItem() != null && event.getItem().getType() == Material.ENCHANTED_BOOK) {
        event.getItem().setType(Material.BOOK);
        event.getPlayer().sendMessage(ChatColor.GREEN + "You reverted this book to its original form!");
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {

    if ((event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))) {
      return;
    }

    Player player = event.getPlayer();

    HCFProfile hcfProfile = HCFProfile.get(player);

    switch (event.getBlock().getType()) {
      case DIAMOND_ORE: {
        hcfProfile.addDiamond(1);
        hcfProfile.addOreMine();
        break;
      }
      case GOLD_ORE: {
        hcfProfile.addGold(1);
        hcfProfile.addOreMine();
        break;
      }
      case IRON_ORE: {
        hcfProfile.addIron(1);
        hcfProfile.addOreMine();
        break;
      }
      case COAL_ORE: {
        hcfProfile.addCoal(1);
        hcfProfile.addOreMine();
        break;
      }
      case REDSTONE_ORE:
      case GLOWING_REDSTONE_ORE: {
        hcfProfile.addRedstone(1);
        hcfProfile.addOreMine();
        break;
      }
      case LAPIS_ORE: {
        hcfProfile.addLapis(1);
        hcfProfile.addOreMine();
        break;
      }
      case EMERALD_ORE: {
        hcfProfile.addEmerald(1);
        hcfProfile.addOreMine();
        break;
      }
    }

    if (hcfProfile.getCurrentOreMines() == 20) {
      hcfProfile.setCurrentOreMines(0);
      hcfProfile.addGems(1);
      player.sendMessage(CC.translate("&eYou have received &61&e gems."));
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
    }
  }

}