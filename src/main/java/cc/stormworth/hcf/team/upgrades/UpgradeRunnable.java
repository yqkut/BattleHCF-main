package cc.stormworth.hcf.team.upgrades;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.player.Players;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

public class UpgradeRunnable implements Runnable{

    @Override
    public void run() {

        for(Player player : Bukkit.getOnlinePlayers()){
            Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());

            if(teamAt == null) continue;

            if(player.hasMetadata("activeeffects") && !teamAt.isMember(player.getUniqueId())){
                removeEffects(teamAt, player);
            }else if (teamAt.isMember(player.getUniqueId()) && !player.hasMetadata("activeeffects")){
                giveEffects(teamAt, player);
            }
        }
    }

    private void giveEffects(Team team, Player player){
        player.setMetadata("activeeffects", new FixedMetadataValue(Main.getInstance(), true));

        team.getActiveEffects().forEach((potionEffectType, level) ->
                Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(potionEffectType, Integer.MAX_VALUE, level - 1)));
    }

    private void removeEffects(Team team, Player player){

        player.removeMetadata("activeeffects", Main.getInstance());

        team.getActiveEffects().forEach((potionEffectType, level) -> {

            PotionEffect activeEffect = Players.getActivePotionEffect(player, potionEffectType);

            if (activeEffect != null) {
                if (activeEffect.getAmplifier() < (level - 1)) {
                    return;
                }
            }

            player.removePotionEffect(potionEffectType);
        });

    }
}
