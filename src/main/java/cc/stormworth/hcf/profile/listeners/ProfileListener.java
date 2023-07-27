package cc.stormworth.hcf.profile.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.pvptimer.PvPTimer;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import com.mongodb.BasicDBObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProfileListener implements Listener {

  private final Main plugin;

  public ProfileListener(Main plugin) {

    this.plugin = plugin;

    HCFProfile.collection.createIndex(new BasicDBObject("uuid", 1));

    plugin.getServer().getPluginManager().registerEvents(this, Main.getInstance());
  }

  @EventHandler
  public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
    HCFProfile profile = new HCFProfile(event.getName(), event.getUniqueId());

    if (!profile.isLoaded()) {
      event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
      event.setKickMessage(ChatColor.RED + "Failed to load your profile.");
      return;
    }

    if (profile.isDeathBanned() && (Main.getInstance().getServerHandler().isEOTW() || Main.getInstance().getServerHandler().isPreEOTW())){
      event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
      event.setKickMessage(ChatColor.RED + "You can't come in right now");
    }

    profile.setName(event.getName());


    HCFProfile.getProfiles().put(profile.getUuid(), profile);

    if (profile.getTeam() != null){
      if (!profile.getTeam().contains(event.getUniqueId())){
        profile.setTeam(null);
      }
    }else{
      for (Team team : plugin.getTeamHandler().getTeams()) {
        if (team.contains(profile.getUuid())) {
          profile.setTeam(team);
        }
      }
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event){
    Player player = event.getPlayer();

    HCFProfile profile = HCFProfile.get(player);

    if (profile == null) {
      player.kickPlayer(ChatColor.RED + "Failed to load your profile.");
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {

    Player player = event.getPlayer();

    HCFProfile profile = HCFProfile.getProfiles().remove(player.getUniqueId());

    if (profile == null) {
      return;
    }

    if (profile.isDeathBanned()) {
      plugin.getDeathbanRunnable().getOnlineDeathBannedPlayers().remove(player.getUniqueId());
    }

    if (profile.hasPvPTimer()){
      PvPTimer timer = profile.getPvpTimer();

      if(timer.isPaused()){

        long time = System.currentTimeMillis() - timer.getPausedAt();

        timer.setEndAt(timer.getEndAt() + time);
      }
    }

    if(profile.getPlaySession() != null){
      profile.getPlaySession().setEndTime(System.currentTimeMillis());

      profile.setPlayTime(profile.getPlayTime() + profile.getPlaySession().getTime());
    }

    profile.getEnchantments().values().forEach(enchantment -> enchantment.forEach(enchantment1 -> {
      player.removePotionEffect(enchantment1.getEffect().getType());
    }));

    profile.asyncSave();
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    
    Player damager = null;
    
    if(event.getDamager() instanceof Projectile){
      Projectile projectile = (Projectile) event.getDamager();
      
      if (projectile.getShooter() instanceof Player) {
        damager = (Player) projectile.getShooter();
      }
    }else if ((event.getDamager() instanceof Player)) {
      damager = (Player) event.getDamager();
    }
    
    if (damager == null) {
      return;
    }
    
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (event.isCancelled()) {
      return;
    }

    Player damaged = (Player) event.getEntity();

    HCFProfile profile = HCFProfile.get(damaged);
    HCFProfile profileDamager = HCFProfile.get(damager);

    Team damagerFaction = profileDamager.getTeam();

    if (damagerFaction != null && profile.getTeam() != null) {

      if (damagerFaction.contains(damaged)) {
        return;
      }

      if (!damagerFaction.getAllies().isEmpty() && damagerFaction.isAlly(profile.getTeam())) {
        return;
      }
    }


    profile.setLastDamager(damager);
    profileDamager.setLastDamaged(damaged);

    profileDamager.setLastDamagedTime(System.currentTimeMillis());
    profile.setLastDamagerTime(System.currentTimeMillis());
  }

  @EventHandler
  public void onClick(InventoryClickEvent event){
    Player player = (Player) event.getWhoClicked();

    HCFProfile profile = HCFProfile.get(player);

    if (profile == null) {
      return;
    }

    Inventory openInventory = player.getOpenInventory().getTopInventory();

    Inventory inventory = event.getClickedInventory();

    if (inventory == null) {
      return;
    }

    if (!openInventory.getName().equalsIgnoreCase(CC.translate("&a&lClaimed Items"))) {
      return;
    }

    ClickType clickType = event.getClick();

    if (inventory == openInventory) {

      if (clickType == ClickType.NUMBER_KEY){
        event.setCancelled(true);
        return;
      }

      ItemStack itemStack = event.getCurrentItem();
      ItemStack cursor = event.getCursor();

      if ((cursor != null && cursor.getType() != Material.AIR) && !profile.getNoReclaimedItems().contains(cursor)){
        event.setCancelled(true);
        return;
      }

      if ((itemStack != null && itemStack.getType() != Material.AIR) && !profile.getNoReclaimedItems().contains(itemStack)){
        event.setCancelled(true);
      }

    }else if (inventory == player.getInventory() && (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();
    HCFProfile profile = HCFProfile.get(player);

    Inventory inventory = event.getInventory();

    if (inventory.getName().equalsIgnoreCase(CC.translate("&a&lClaimed Items"))) {
      profile.getNoReclaimedItems().clear();

      for (int i = 0; i < inventory.getSize(); i++) {
        if (inventory.getItem(i) != null) {
          profile.getNoReclaimedItems().add(inventory.getItem(i));
        }
      }
    } else if (inventory.getName().contains(CC.translate("&ePrivate Chest"))) {
      int number = Integer.parseInt(
          inventory.getName().replace(CC.translate("&ePrivate Chest "), ""));

      profile.getPrivateChests().get(number - 1).getContents().clear();

      for (int i = 0; i < inventory.getSize(); i++) {
        if (inventory.getItem(i) != null) {
          profile.getPrivateChests().get(number - 1).getContents().add(inventory.getItem(i));
        }
      }
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event){

    Location to = event.getTo();
    Location from = event.getFrom();

    if(to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()){
      return;
    }

    HCFProfile profile = HCFProfile.get(event.getPlayer());

    if(!profile.hasPvPTimer()){
      return;
    }

    PvPTimer pvptimer = profile.getPvpTimer();

    if (DTRBitmask.SAFE_ZONE.appliesAt(to) && !pvptimer.isPaused()) {
      pvptimer.pause();
    }else if(DTRBitmask.SAFE_ZONE.appliesAt(from) && pvptimer.isPaused()){
      pvptimer.resume();
    }
  }


}