package cc.stormworth.hcf.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import com.google.common.collect.Lists;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;

public class NearCommand {

    private static final double DISTANCE = 200.0;

    @Command(names = {"near"}, permission = "WARRIOR")
    public static void near(Player player) {

        List<String> nearby = getNearbyEnemies(player);

        player.sendMessage(CC.translate("&eNearby players: &a(" + nearby.size() + ")"));
        player.sendMessage(CC.translate("&7 " + (nearby.isEmpty() ? "&cNone" : String.join("&e, &a", nearby))));

    }

    private static List<String> getNearbyEnemies(Player player){
        List<String> players = Lists.newArrayList();
        Collection<Entity> nearby = player.getNearbyEntities(DISTANCE, DISTANCE, DISTANCE);
        for(final Entity entity : nearby){
            if(entity instanceof Player){
                final Player target = (Player) entity;
                if(!target.canSee(player)){
                    continue;
                }
                if(!player.canSee(target)){
                    continue;
                }
                if(target.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                    continue;
                }
                players.add(target.getName() + " (" + player.getLocation().distance(target.getLocation()) + ")");
            }
        }
        return players;
    }
}
