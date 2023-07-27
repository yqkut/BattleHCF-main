package cc.stormworth.hcf.pvpclasses.event;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.RestoreEffect;
import cc.stormworth.hcf.util.player.Players;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;
import java.util.function.Predicate;

public class EffectRestorer implements Listener {

    @Getter
    private final Table<UUID, Effect, RestoreEffect> restores;

    public EffectRestorer(final Main plugin) {
        this.restores = HashBasedTable.create();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setRestoreEffect(Player player, PotionEffect effect) {
        PotionEffect activeEffect = Players.getActivePotionEffect(player, effect.getType());

        if (activeEffect == null){
            player.addPotionEffect(effect, true);
            return;
        }

        Team team = Main.getInstance().getTeamHandler().getTeam(player);

        Predicate<Player> condition = other -> true;

        if(team != null && team.isInClaim(player) && team.getActiveEffects().containsKey(effect.getType())){
            condition = team::isInClaim;
        }

        boolean override = effect.getAmplifier() > activeEffect.getAmplifier();

        if(override){
            restores.put(player.getUniqueId(), new Effect(effect.getType(), effect.getAmplifier()), new RestoreEffect(activeEffect, condition));
            player.addPotionEffect(effect, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        RestoreEffect previous = this.restores.remove(player.getUniqueId(), Effect.getByPotionEffect(event.getEffect()));

        if (previous != null && previous.getCondition().test(player)) {
            TaskUtil.run(Main.getInstance(), () -> player.addPotionEffect(previous.getEffect(), true));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPotionRemove(PotionEffectRemoveEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        RestoreEffect previous = this.restores.remove(player.getUniqueId(), Effect.getByPotionEffect(event.getEffect()));

        if (previous != null && previous.getCondition().test(player)) {
            TaskUtil.run(Main.getInstance(), () -> player.addPotionEffect(previous.getEffect(), true));
        }
    }
}