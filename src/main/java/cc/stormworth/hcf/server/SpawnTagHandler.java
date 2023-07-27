package cc.stormworth.hcf.server;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownType;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SpawnTagHandler {

    private static final Map<String, Long> spawnTags = new HashMap<>();

    public static void removeTag(final Player player) {
        SpawnTagHandler.spawnTags.remove(player.getName());
        CooldownManager.removeCooldown(player.getUniqueId(), CooldownType.COMBAT_TAG);
    }

    public static void addOffensiveSeconds(final Player player, final int seconds) {
        addSeconds(player, seconds);
    }

    public static void addPassiveSeconds(final Player player, final int seconds) {
        if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && !CustomTimerCreateCommand.hasSOTWEnabled(player))
            return;
        addSeconds(player, seconds);
    }

    private static void addSeconds(final Player player, final int seconds) {
        if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && !CustomTimerCreateCommand.hasSOTWEnabled(player))
            return;

        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) return;

        if (isTagged(player)) {
            SpawnTagHandler.spawnTags.put(player.getName(), System.currentTimeMillis() + seconds * 1000L);
        } else {
            player.sendMessage(ChatColor.YELLOW + "You have been spawn-tagged for §c" + seconds + " §eseconds!");
            SpawnTagHandler.spawnTags.put(player.getName(), System.currentTimeMillis() + seconds * 1000L);
        }

        TaskUtil.runAsync(Main.getInstance(), () -> {
            CooldownManager.addCooldown(player.getUniqueId(), CooldownType.COMBAT_TAG, seconds);
        });
    }

    public static long getTag(final Player player) {
        return SpawnTagHandler.spawnTags.get(player.getName()) - System.currentTimeMillis();
    }

    public static boolean isTagged(Player player) {
        if (player != null) {
            return spawnTags.containsKey(player.getName()) && spawnTags.get(player.getName()) > System.currentTimeMillis();
        } else {
            return false;
        }
    }

    public static int getMaxTagTime() {
        return 45;
    }

    public static int getPassiveTime() {
        return 6;
    }

    public static Map<String, Long> getSpawnTags() {
        return SpawnTagHandler.spawnTags;
    }
}