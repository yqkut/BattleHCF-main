package cc.stormworth.hcf.ability.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.ability.impl.BowTeleporter;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Utils;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class AbilityListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (player.getWalkSpeed() < 0.2F) {
      player.setWalkSpeed(0.2F);
    }
  }

  @EventHandler
  public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
    Player player = event.getPlayer();

    String command = event.getMessage().toLowerCase();

    if (command.startsWith("/rename") || command.startsWith("/fix")){
      ItemStack item = player.getItemInHand();

      Ability ability = Ability.getByItem(item);

      if (ability == null) {
        return;
      }

      event.setCancelled(true);

      if (command.startsWith("/rename")) {
        player.sendMessage(CC.translate("&cYou cannot rename this item."));
      } else {
        player.sendMessage(CC.translate("&cYou cannot fix this item."));
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event) {

    Player player = event.getPlayer();

    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

      if (!event.hasItem()
              || !event.getItem().hasItemMeta()
              || !event.getItem().getItemMeta().hasDisplayName()
              || !event.getItem().getItemMeta().hasLore()) {
        return;
      }

      Ability ability = Ability.getByItem(event.getItem());

      if (ability == null) {
        return;
      }

      if (CooldownAPI.hasCooldown(player, ability.getName())) {
        player.sendMessage(ChatColor.RED + "You have to wait " + ScoreFunction.TIME_FANCY.apply(CooldownAPI.getCooldown(player, ability.getName()) / 1000F) +
            " to use it again");
        event.setCancelled(true);
        player.updateInventory();
        return;
      }
    }

    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!event.hasItem() ||
            !event.getItem().hasItemMeta() ||
            !event.getItem().getItemMeta().hasDisplayName() ||
            !event.getItem().getItemMeta().hasLore()) {
      return;
    }

    if (player.getWorld().getName().equalsIgnoreCase("void")) {
      return;
    }

    Ability ability = Ability.getByItem(event.getItem());

    if (ability == null) {
      return;
    }

    if (player.getLocation().getWorld().getEnvironment() == Environment.NETHER) {
      player.sendMessage(CC.RED + "You cannot use abilities in the nether!");
      event.setCancelled(true);
      return;
    }

    if (ability instanceof BowTeleporter) {
      return;
    }

    event.setCancelled(true);
    player.updateInventory();

    if (!(ability instanceof InteractAbility)) {
      return;
    }

    InteractAbility interactAbility = (InteractAbility) ability;

    if (!interactAbility.isEnabled()) {
      player.sendMessage(ChatColor.RED + "This ability is currently disabled.");
      return;
    }

    if (CooldownAPI.hasCooldown(player, "Global")) {
      player.sendMessage(CC.translate("&cYou have to wait " + ScoreFunction.TIME_FANCY.apply(CooldownAPI.getCooldown(player, "Global") / 1000F) +
              " to use &lAnother Ability&c again"));
      return;
    }

    if (CooldownAPI.hasCooldown(player, interactAbility.getName())) {
      player.sendMessage(ChatColor.RED + "You have to wait " + ScoreFunction.TIME_FANCY.apply(CooldownAPI.getCooldown(player, interactAbility.getName()) / 1000F) +
              " to use it again");
      return;
    }

    if (Utils.isEventLocated(player, true)) {
      player.sendMessage(
          CC.RED + "You cannot use this while in warzone and your team is in the event.");
      return;
    }

    if (DTRBitmask.KOTH.appliesAt(event.getPlayer().getLocation())
        || DTRBitmask.CITADEL.appliesAt(event.getPlayer().getLocation())
        || DTRBitmask.CONQUEST.appliesAt(event.getPlayer().getLocation())
        || DTRBitmask.DTC.appliesAt(event.getPlayer().getLocation())) {

      if (DTRBitmask.KOTH.appliesAt(event.getPlayer().getLocation())){
        Team team = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());

        if (team != null) {
          if (!team.getName().equalsIgnoreCase("sky")) {
            interactAbility.handleAbilityRefund(event.getPlayer(), CC.RED + "You cannot use special items in events.", false);
            return;
          }
        }
      }else{
        interactAbility.handleAbilityRefund(event.getPlayer(), CC.RED + "You cannot use special items in events.", false);
        return;
      }
    }

    if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
      interactAbility.handleAbilityRefund(event.getPlayer(),
          CC.RED + "You cannot use special items in spawn.", false);
      return;
    }
    HCFProfile profile = HCFProfile.get(event.getPlayer());

    if (profile.hasPvPTimer()) {
      interactAbility.handleAbilityRefund(event.getPlayer(),
              CC.RED + "You cannot use special items with pvp timer.", false);
      return;
    }
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")
            && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
      interactAbility.handleAbilityRefund(event.getPlayer(),
              CC.RED + "You cannot use special items with sotw timer.", false);
      return;
    }

    if (!Main.getInstance().getMapHandler().isKitMap()){
      if (Main.getInstance().getServerHandler()
              .isWarzone(player.getLocation()) || player.getWorld().getEnvironment() == Environment.NETHER || player.getWorld().getEnvironment() == Environment.THE_END) {
        player.sendMessage(CC.RED + "You cannot use this ability here.");
        return;
      }
    }

    if (CooldownAPI.hasCooldown(player, "NoPowers")) {
      player.sendMessage(CC.translate("&cYou cannot use abilities while"));
      return;
    }

    interactAbility.onInteract(event);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    Player damager = null;
    if (!(event.getDamager() instanceof Player)) {
      if (event.getDamager() instanceof Projectile) {
        Projectile projectile = (Projectile) event.getDamager();
        if (projectile.getShooter() instanceof Player) {
          damager = (Player) projectile.getShooter();
        }
      }
    } else {
      damager = (Player) event.getDamager();
    }

    if (damager == null) {
      return;
    }

    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (event.getDamager() instanceof EnderPearl) {
      return;
    }

    Player victim = (Player) event.getEntity();

    ItemStack item = damager.getItemInHand();

    DamageableAbility ability = null;

    if (event.getDamager() instanceof Arrow) {
      Arrow arrow = (Arrow) event.getDamager();

      if (arrow.getShooter() instanceof Player) {
        if (arrow.hasMetadata("BowTeleporter")) {
          ability = (DamageableAbility) Ability.getByName("BowTeleporter");
        }
      }
    }

    if (ability == null) {
      if (item == null || !item.hasItemMeta()) {
        return;
      }

      if (damager.getWorld().getName().equalsIgnoreCase("void")) {
        return;
      }

      if (!(Ability.getByItem(item) instanceof DamageableAbility)) {
        return;
      }

      if (damager.getLocation().getWorld().getEnvironment() == Environment.NETHER) {
        damager.sendMessage(CC.RED + "You cannot use abilities in the nether!");
        return;
      }

      ability = (DamageableAbility) Ability.getByItem(item);

      if (ability == null) {
        return;
      }
    }

    if (!ability.isEnabled()) {
      damager.sendMessage(ChatColor.RED + "This ability is currently disabled.");
      return;
    }

    if (CooldownAPI.hasCooldown(damager, "Global")) {
      damager.sendMessage(CC.translate("&cYou have to wait " + ScoreFunction.TIME_FANCY.apply((float) (CooldownAPI.getCooldown(damager, "Global") / 1000F)) +
              " to use &lAnother Ability&c again"));
      return;
    }

    if (CooldownAPI.hasCooldown(damager, ability.getName())) {
      damager.sendMessage(ChatColor.RED + "You have to wait " + ScoreFunction.TIME_FANCY.apply(CooldownAPI.getCooldown(damager, ability.getName()) / 1000F) +
              " to use it again");
      return;
    }

    if (Utils.isEventLocated(damager, true)) {
      damager.sendMessage(
          CC.RED + "You cannot use this while in warzone and your team is in the event.");
      return;
    }

    if (DTRBitmask.KOTH.appliesAt(victim.getLocation()) || DTRBitmask.CITADEL.appliesAt(
        victim.getLocation()) || DTRBitmask.CONQUEST.appliesAt(victim.getLocation())
        || DTRBitmask.DTC.appliesAt(victim.getLocation())) {
      ability.handleAbilityRefund(damager, CC.RED + "You cannot use special items against players in events.", false);
      event.setCancelled(true);
      return;
    }

    if (DTRBitmask.KOTH.appliesAt(victim.getLocation())
            || DTRBitmask.CITADEL.appliesAt(victim.getLocation())
            || DTRBitmask.CONQUEST.appliesAt(victim.getLocation())
            || DTRBitmask.DTC.appliesAt(victim.getLocation())) {

      if (DTRBitmask.KOTH.appliesAt(victim.getLocation())){
        Team team = LandBoard.getInstance().getTeam(victim.getLocation());

        if (team != null) {
          if (!team.getName().equalsIgnoreCase("sky")) {
            ability.handleAbilityRefund(damager, CC.RED + "You cannot use special items against players in events.", false);
            return;
          }
        }
      }else{
        ability.handleAbilityRefund(damager, CC.RED + "You cannot use special items against players in events.", false);
        return;
      }
    }

    if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation())) {
      ability.handleAbilityRefund(damager,
          CC.RED + "You cannot use special items against players in spawn.", false);
      return;
    }
    HCFProfile profile = HCFProfile.get(victim);

    if (profile.hasPvPTimer()) {
      ability.handleAbilityRefund(damager,
          CC.RED + "You cannot use special items against players with pvp timer.", false);
      return;
    }
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")
        && !CustomTimerCreateCommand.hasSOTWEnabled(victim.getUniqueId())) {
      ability.handleAbilityRefund(damager,
          CC.RED + "You cannot use special items against players with sotw timer.", false);
      return;
    }
    Team damagerFaction = Main.getInstance().getTeamHandler().getTeam(damager);
    if (damagerFaction != null) {
      Team targetFaction = Main.getInstance().getTeamHandler().getTeam(victim);
      if (damagerFaction == targetFaction) {
        damager.sendMessage(CC.RED + "You cannot use special items against your team members.");
        return;
      }
      if (!damagerFaction.getAllies().isEmpty() && damagerFaction.isAlly(targetFaction)) {
        damager.sendMessage(CC.RED + "You cannot use special items against your team allies.");
        return;
      }
    }

    if (!Main.getInstance().getMapHandler().isKitMap()){
      if (Main.getInstance().getServerHandler()
              .isWarzone(damager.getLocation()) || damager.getWorld().getEnvironment() == Environment.NETHER || damager.getWorld().getEnvironment() == Environment.THE_END) {
        damager.sendMessage(CC.RED + "You cannot use this ability here.");
        return;
      }
    }

    if (CooldownAPI.hasCooldown(damager, "NoPowers")) {
      damager.sendMessage(CC.translate("&cYou cannot use abilities while"));
      return;
    }

    ability.onEntityDamageByEntity(event);
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    HCFProfile profile = HCFProfile.get(player);

    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
      if (e.getDamager() instanceof Player) {
        Player damager = (Player) e.getDamager();

        Team damagerFaction = Main.getInstance().getTeamHandler().getTeam(damager);
        if (damagerFaction != null) {
          Team targetFaction = Main.getInstance().getTeamHandler().getTeam(player);
          if (damagerFaction == targetFaction) {
            return;
          }
          if (!damagerFaction.getAllies().isEmpty() && damagerFaction.isAlly(targetFaction)) {
            return;
          }
        }
      }
    }

    if (profile.getCountdown() != null) {

      if (profile.getTeleport() == null) {
        return;
      }

      if (!profile.getTeleport().isCancelledOnDamage()) {
        return;
      }

      profile.getCountdown().cancel();
      profile.setCountdown(null);

      player.sendMessage(CC.translate("&cYou teleport has been cancelled due to damage."));
    }
  }

  @EventHandler
  public void onPlayerDeathEvent(PlayerDeathEvent event){
    Player player = event.getEntity();
    player.setMetadata("deathAt", new FixedMetadataValue(Main.getInstance(), System.currentTimeMillis()));
  }
}