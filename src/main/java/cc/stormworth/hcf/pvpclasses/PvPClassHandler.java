package cc.stormworth.hcf.pvpclasses;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.pvpclasses.pvpclasses.*;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.Utils;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
public class PvPClassHandler implements Runnable, Listener {

  @Getter private static final Map<String, PvPClass> equippedKits = new HashMap<>();
  private final Map<UUID, PvPClass.SavedPotion> savedPotions = new HashMap<>();

  private final MinerClass miner = new MinerClass();
  private final ArcherClass archer = new ArcherClass();
  private final BardClass bard = new BardClass();
  private final RogueClass rogue = new RogueClass();
  @Getter
  List<PvPClass> pvpClasses = new ArrayList<>();

  public PvPClassHandler() {
    this.pvpClasses.add(miner);
    this.pvpClasses.add(archer);
    this.pvpClasses.add(bard);
    this.pvpClasses.add(rogue);
    if (Main.getInstance().getMapHandler().isKitMap()) {
      this.pvpClasses.add(new DuelistClass());
    }

    for (PvPClass pvpClass : pvpClasses) {
      Bukkit.getPluginManager().registerEvents(pvpClass, Main.getInstance());
    }

    Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), this, 0L, 1L);
    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }

  public static PvPClass getPvPClass(Player player) {
    return (equippedKits.getOrDefault(player.getName(), null));
  }

  public PvPClass getPvpClazz(Class<? extends PvPClass> clazz) {
    return this.pvpClasses.stream().filter(pvpClass -> pvpClass.getClass() == clazz).findFirst()
        .orElse(null);
  }

  public static boolean hasKitOn(Player player, PvPClass pvpClass) {
    return (equippedKits.containsKey(player.getName())
        && equippedKits.get(player.getName()) == pvpClass);
  }

  @Override
  public void run() {
    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
      // Remove kit if player took off armor, otherwise .tick();
      if (equippedKits.containsKey(player.getName())) {
        PvPClass pvpClass = equippedKits.get(player.getName());

        if (!pvpClass.qualifies(player.getInventory())) {
          equippedKits.remove(player.getName());
          player.sendMessage("");
          player.sendMessage(
              CC.translate("&7 • &6&lClass: &e" + pvpClass.getName() + " &c(Disabled)"));
          player.sendMessage("");
          pvpClass.remove(player);

          Team team = Main.getInstance().getTeamHandler().getTeam(player);
          if (team != null) {
            pvpClass.removeLimit(team);
          }

          PvPClass.removeInfiniteEffects(player);
        } else {
          pvpClass.tick(player);
        }
      } else {
        // Start kit warmup
        for (PvPClass pvpClass : pvpClasses) {

          if (!pvpClass.qualifies(player.getInventory())) {
            pvpClass.getLimitMessage().remove(player.getUniqueId());
          }

          if (pvpClass.qualifies(player.getInventory()) && pvpClass.canApply(player)) {
            Team team = Main.getInstance().getTeamHandler().getTeam(player);

            if (team != null) {
              if (pvpClass.hasLimit(team)) {
                if (!pvpClass.getLimitMessage().contains(player.getUniqueId())) {

                  StringJoiner joiner = new StringJoiner(CC.translate("&7, "));

                  team.getOnlineMembers().stream().filter(online -> hasKitOn(online, pvpClass))
                      .forEach(online -> joiner.add(online.getName()));

                  player.sendMessage(new String[]{
                      CC.RED + "Your team already exceeded the limit of " + pvpClass.getName() + " class! (" + pvpClass.getLimit() + ")",
                      CC.RED + "- " + joiner
                  });
                  pvpClass.getLimitMessage().add(player.getUniqueId());
                }
                break;
              }
              pvpClass.addLimit(team);
            }

            pvpClass.apply(player);
            PvPClassHandler.getEquippedKits().put(player.getName(), pvpClass);

            /*if (Main.getInstance().getMapHandler().isKitMap()) {
              for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null && armor.hasItemMeta() && armor.getItemMeta().getLore() != null
                    && !armor.getItemMeta().getLore().isEmpty()) {
                  if (armor.getItemMeta().getLore().contains(CC.translate("&6&lRanger+ unique class"))) {
                    break;
                  } else if (armor.getItemMeta().getLore().contains(CC.translate("&6&lPhantom+ unique class"))) {
                    break;
                  }
                }
              }
            }*/

            player.sendMessage("");
            player.sendMessage(
                CC.translate("&7 • &6&lClass: &e" + pvpClass.getName() + " &a(Enabled)"));
            player.sendMessage("");
            break;
          }
        }
      }
    }
    checkSavedPotions();
  }

  public void checkSavedPotions() {
    Iterator<Map.Entry<UUID, PvPClass.SavedPotion>> idIterator = savedPotions.entrySet().iterator();
    while (idIterator.hasNext()) {
      Map.Entry<UUID, PvPClass.SavedPotion> id = idIterator.next();
      Player player = Bukkit.getPlayer(id.getKey());
      if (player != null && player.isOnline()) {
        if (id.getValue().getTime() < System.currentTimeMillis() && !id.getValue().isPerm()) {
          if (player.hasPotionEffect(id.getValue().getPotionEffect().getType())) {
            player.getActivePotionEffects().forEach(potion -> {
              PotionEffect restore = id.getValue().getPotionEffect();
              if (potion.getType() == restore.getType()
                  && potion.getDuration() < restore.getDuration()
                  && potion.getAmplifier() <= restore.getAmplifier()) {
                player.removePotionEffect(restore.getType());
              }
            });
          }

          if (player.addPotionEffect(id.getValue().getPotionEffect(), true)) {
            Bukkit.getLogger().info(
                id.getValue().getPotionEffect().getType() + ", " + id.getValue().getPotionEffect()
                    .getDuration() + ", " + id.getValue().getPotionEffect().getAmplifier());
            idIterator.remove();
          }
        }
      } else {
        idIterator.remove();
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getPlayer().getItemInHand() == null || !event.getAction().name().contains("RIGHT")) {
      return;
    }

    for (PvPClass pvPClass : pvpClasses) {
      if (hasKitOn(event.getPlayer(), pvPClass) && pvPClass.getConsumables() != null
          && pvPClass.getConsumables().contains(event.getPlayer().getItemInHand().getType())) {

        if (CooldownAPI.hasCooldown(event.getPlayer(), "anti_class")) {
          event.getPlayer().sendMessage(CC.translate("&cYou cannot use this item for another &l"
              + ScoreFunction.TIME_FANCY.apply(CooldownAPI.getCooldown(event.getPlayer(), "anti_class") / 1000F) + "&c."));
          break;
        }

        if (pvPClass.itemConsumed(event.getPlayer(), event.getItem().getType())) {
          Utils.removeOneItem(event.getPlayer());
          break;
        }
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (equippedKits.containsKey(event.getPlayer().getName())) {
      Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());
      if (team != null) {
        equippedKits.get(event.getPlayer().getName()).removeLimit(team);
      }
      equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
      equippedKits.remove(event.getPlayer().getName());
    }
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent event) {
    if (equippedKits.containsKey(event.getPlayer().getName())) {
      Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());
      if (team != null) {
        equippedKits.get(event.getPlayer().getName()).removeLimit(team);
      }

      equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
      equippedKits.remove(event.getPlayer().getName());
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (equippedKits.containsKey(event.getPlayer().getName())) {
      equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
      equippedKits.remove(event.getPlayer().getName());
    }

    /*for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
      if (potionEffect.getDuration() > 1_000_000) {
        //event.getPlayer().removePotionEffect(potionEffect.getType());
      }
    }*/
  }
}