package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class EndEventCommand {

    @Getter
    public static LinkedHashMap<UUID, Integer> hits = new LinkedHashMap<>();
    @Getter
    public static boolean started = false;

    public static LinkedHashMap<UUID, Integer> sortByValues(Map<UUID, Integer> map) {
        LinkedList<Map.Entry<UUID, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<UUID, Integer> sortedHashMap = new LinkedHashMap<>();
        Iterator<Map.Entry<UUID, Integer>> iterator = list.iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

    @Command(names = {"endevent start"}, hidden = true, permission = "op")
    public static void spawnDragon(final Player sender) {
        EndEventCommand.hits.clear();
        if (started) {
            sender.sendMessage(ChatColor.RED + "There's already an enderdragon!");
            return;
        }
        if (sender.getWorld().getEnvironment() == World.Environment.THE_END) {
            started = true;
            EnderDragon dragon = (EnderDragon) sender.getWorld().spawnEntity(sender.getLocation(), EntityType.ENDER_DRAGON);
            ;

            dragon.setMaxHealth(2000);
            dragon.setHealth(2000);
            dragon.setRemoveWhenFarAway(false);

            sender.sendMessage(ChatColor.GREEN + "You have unleashed the beast.");
        } else {
            sender.sendMessage(ChatColor.RED + "You must be in the end to spawn the Enderdragon!");
        }
    }

    @Command(names = {"endevent stop"}, hidden = true, permission = "op")
    public static void stop(final Player sender) {
        EndEventCommand.hits.clear();
        if (sender.getWorld().getEnvironment() != World.Environment.THE_END) {
            sender.sendMessage(ChatColor.RED + "You must be in the end to spawn the Enderdragon!");
            return;
        }
        if (!started) {
            sender.sendMessage(ChatColor.RED + "There's not an enderdragon!");
            return;
        }
        for (Entity entity : sender.getWorld().getEntitiesByClasses(EnderDragon.class)) {
            entity.remove();
        }
        started = false;
        sender.sendMessage(CC.YELLOW + "End Event Ended.");
    }
}