package cc.stormworth.hcf.ability.impl.merge;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.misc.request.Request;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class MergeAbility extends InteractAbility {

  public MergeAbility() {
    super(
        "Merge", "&bMerge",
        Lists.newArrayList(
            "",
            "&7Ancestral unions? Things from another planet or even new technology? We don't ",
            "&7know, use this ability to join your partner and",
            "&7receive special effects, but be careful!",
            "&7You are fully merged, if he gets hit, you take damage.",
            ""
        ),
        new ItemBuilder(Material.STONE_SWORD).name("&bMerge").build(),
        TimeUtil.parseTimeLong("12m"));
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (!event.getAction().name().contains("RIGHT_CLICK")) {
      return;
    }

    if (CooldownAPI.hasCooldown(player, getName())) {
      player.sendMessage(ChatColor.RED + "You have to wait " + TimeUtil.millisToRoundedTime(
          CooldownAPI.getCooldown(player, getName())) +
          " to use it again");
      return;
    }

    if (Merge.isMerged(player)) {
      player.sendMessage(CC.translate("&cYou already merged."));
      return;
    }

    if (Request.hasRequest(player)) {
      Request request = Request.getRequest(player);

      Player other = request.getRequesterPlayer();

      if (other == null) {
        return;
      }

      player.sendMessage(CC.translate("&6&l[&e✷&6&l] &eYou have merge with &6&l" + other.getName()));

      consume(player);
      consume(other);
      request.execute();
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(player);

    if (team == null) {
      player.sendMessage(CC.translate("&cYou must be in a team to use this ability."));
      return;
    }

    Player target = null;

    for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
      if (entity == player) {
        continue;
      }

      if (!(entity instanceof Player)) {
        return;
      }

      target = (Player) entity;

      if (!target.getInventory().contains(getItem()) || !team.isMember(target.getUniqueId())) {
        target = null;
      }

      if (target != null) {
        break;
      }
    }

    if (target == null) {
      player.sendMessage(CC.translate("&cNot found near teammate."));
      return;
    }

    Team otherTeam = Main.getInstance().getTeamHandler().getTeam(target);

    if (otherTeam == null) {
      player.sendMessage(CC.translate("&c" + target.getName() + " not have faction."));
      return;
    }

    if (!team.isMember(target.getUniqueId())) {
      player.sendMessage(CC.translate("&c" + target.getName() + " player is not in your team."));
      return;
    }

    if (Merge.isMerged(target)) {
      player.sendMessage(
          CC.translate("&e" + target.getName() + " &calready merged with other player."));
      return;
    }

    if (Request.hasRequested(player)) {
      player.sendMessage(CC.translate("&cYou already requested."));
      return;
    }
    Clickable clickable = new Clickable("&6&l[&e✷&6&l] " + player.getName()
        + " &ahas requested to merge with you.\n");

    clickable.add("&ePlease use the same ability to confirm.");

    Request request = new Request(clickable,
            player.getUniqueId(), target.getUniqueId(),
            System.currentTimeMillis() + TimeUtil.parseTimeLong("20s"));

    request.addAction((players) -> {
      Player requester = players.get(0);
      Player requested = players.get(1);

      if (Merge.isMerged(requester) || Merge.isMerged(requested)) {
        return;
      }

      for (PotionEffect potionEffect : getPotionEffects()) {
        requester.addPotionEffect(potionEffect);
        requested.addPotionEffect(potionEffect);
      }

      requester.sendMessage(
          CC.translate("&aYou have been merged with &e" + requested.getName() + "&a!"));

      requested.sendMessage(
          CC.translate("&aYou have been merged with &e" + requester.getName() + "&a!"));

      new Merge(requester, requested);

      Bukkit.broadcastMessage(CC.translate(
          "&6&l" + requester.getName() + " &ehas been merged with &6&l"
              + requested.getName() + "&e!"));

      Team teamAt = LandBoard.getInstance().getTeam(requester.getLocation());

        if (teamAt != null) {
          teamAt.getOnlineMembers().forEach(other -> {
            TitleBuilder titleBuilder = new TitleBuilder("&c" + requester.getName() +" &ehas merged with &c" + requested.getName(), "", 10, 10, 10);

            titleBuilder.send(other);
          });
        }

      requested.playSound(requested.getLocation(), Sound.LEVEL_UP, 1, 1);
      requester.playSound(requester.getLocation(), Sound.LEVEL_UP, 1, 1);

      CooldownAPI.setCooldown(requested, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
      CooldownAPI.setCooldown(requester, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");

      requester.removeMetadata("request", Main.getInstance());
    });

    player.sendMessage(CC.translate("&aSend merge request to &e" + target.getName()));

    player.setMetadata("noDropMerge", new FixedMetadataValue(Main.getInstance(), System.currentTimeMillis() + TimeUtil.parseTimeLong("3m")));

    request.send();
  }

  @EventHandler
  public void onPlayerDrop(PlayerDropItemEvent event){
    Player player = event.getPlayer();

    ItemStack item = event.getItemDrop().getItemStack();
    if(player.hasMetadata("noDropMerge")){
      long time = player.getMetadata("noDropMerge").get(0).asLong();

      if(time > System.currentTimeMillis()){
        if(isItem(item)){
          player.sendMessage(CC.translate("&cYou can't drop this item while you're merging."));
          event.setCancelled(true);
        }
      }else{
        player.removeMetadata("noDropMerge", Main.getInstance());
      }
    }
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {

    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player damaged = (Player) event.getEntity();

    if (!Merge.isMerged(damaged)) {
      return;
    }

    Merge merge = Merge.getMerge(damaged);

    if (merge == null) {
      return;
    }

    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
      if (e.getDamager() instanceof Player) {
        Player damager = (Player) e.getDamager();

        if (damaged.hasMetadata("damager")) {
          if (damaged.getMetadata("damager").get(0).asString().equals(damager.getName())) {
            damaged.removeMetadata("damager", Main.getInstance());
            return;
          }
        }

        if (merge.getOtherPlayer(damaged).equals(damager.getUniqueId())) {
          return;
        }
      }
    }

    if (merge.isExpired()) {
      merge.remove();
      return;
    }

    Player merged = Bukkit.getPlayer(merge.getOtherPlayer(damaged));

    if (merged == null) {
      return;
    }

    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
      if (e.getDamager() instanceof Player) {
        merged.setMetadata("damager", new FixedMetadataValue(Main.getInstance(),
            ((Player) e.getDamager()).getName()));
        merged.damage(event.getDamage(), e.getDamager());
      }
    } else {
      merged.damage(event.getDamage(), null);
    }
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();

    if (!Merge.isMerged(player)) {
      return;
    }

    Merge merge = Merge.getMerge(player);

    Merge.removeMerge(merge);

    Player merged = Bukkit.getPlayer(merge.getOtherPlayer(player));

    if (merged == null) {
      return;
    }

    merged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
    merged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
    merged.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 1));

    for (PotionEffect potionEffect : getPotionEffects()) {
      player.removePotionEffect(potionEffect.getType());
    }
  }

  @Override
  public List<PotionEffect> getPotionEffects() {
    return Lists.newArrayList(
        new PotionEffect(PotionEffectType.ABSORPTION, 20 * 20, 1),
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 1),
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 1),
        new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1)
    );
  }
}

