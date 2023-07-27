package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.brewingstand.BrewingStandRunnable;
import cc.stormworth.hcf.persist.RedisSaveTask;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.misc.SetupStage;
import com.mongodb.DB;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class DevCommand {
    @Command(names = {"dev setupmode"}, permission = "console", hidden = true, async = true)
    public static void setupmode(final CommandSender sender) {
        SetupStage.setupmode = true;

        Bukkit.broadcastMessage(CC.YELLOW + "The setup mode has been " + (SetupStage.setupmode ? "&aEnabled" : "&cDisabled"));

        if (SetupStage.getStage() == SetupStage.NONE) SetupStage.setStage(1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp()) continue;
            SetupStage.giveItem(player);
        }

        Bukkit.shutdown();
    }

    @Command(names = "brewingstand toggle", permission = "op")
    public static void toggle(Player player){
        BrewingStandRunnable.setRunning(!BrewingStandRunnable.isRunning());

        player.sendMessage(ChatColor.GREEN + "BrewingStandRunnable is now " + (BrewingStandRunnable.isRunning() ? "running" : "stopped"));
    }

    private static boolean upgrades = true;

    @Command(names = "toggleupgrades", permission = "op")
    public static void toggleUpgrades(Player player){
        if(upgrades) {
            HandlerList.unregisterAll(Main.getInstance().getUpgradeListener());
            upgrades = false;
            player.sendMessage(CC.RED + "Upgrades are now disabled.");
        } else {
            Bukkit.getPluginManager().registerEvents(Main.getInstance().getUpgradeListener(), Main.getInstance());
            upgrades = true;
            player.sendMessage(CC.GREEN + "Upgrades are now enabled.");
        }
    }

    @Command(names = {"dev test"}, permission = "op", hidden = true, async = true)
    public static void clearpersist(final Player player) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 999999, 1));

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AttributeMapServer attributemapserver = (AttributeMapServer) entityPlayer.getAttributeMap();
        Set set = attributemapserver.getAttributes();

        for (Object genericInstance : set) {
            IAttribute attribute = ((AttributeInstance) genericInstance).getAttribute();
            if (attribute.getName().equals("generic.maxHealth")) {
                set.remove(genericInstance);
                break;
            }
        }

        set.add(new AttributeModifiable(entityPlayer.getAttributeMap(), (new AttributeRanged("generic.maxHealth", 4, 0.0D, Float.MAX_VALUE)).a("Max Health").a(true)));

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(entityPlayer.getId(), set));
    }

    @Command(names = {"dev test2"}, permission = "op", hidden = true, async = true)
    public static void clearpersist2(final Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AttributeMapServer attributemapserver = (AttributeMapServer) entityPlayer.getAttributeMap();
        Set set = attributemapserver.getAttributes();

        for (Object genericInstance : set) {
            IAttribute attribute = ((AttributeInstance) genericInstance).getAttribute();
            if (attribute.getName().equals("generic.maxHealth")) {
                set.remove(genericInstance);
                break;
            }
        }

        set.add(new AttributeModifiable(entityPlayer.getAttributeMap(), (new AttributeRanged("generic.maxHealth", 20, 0.0D, Float.MAX_VALUE)).a("Max Health").a(true)));

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(entityPlayer.getId(), set));

        /*DataWatcher datawatcher = entityPlayer.getDataWatcher();

        datawatcher.watch(6, 1.0F);
        */
        //entityPlayer.playerConnection.sendPacket(new PacketPlayOutUpdateHealth(1.0F, datawatcher.getByte(7), datawatcher.getFloat(6)));
    }

    @Command(names = {"dev resetdatabases"}, permission = "console", hidden = true, async = true)
    public static void resetdatabases(final CommandSender sender) {
        try {
            CorePlugin.getInstance().runRedisCommand(jedis -> {
                jedis.flushDB();
                return null;
            });
            LandBoard.getInstance().loadFromTeams();
            Main.getInstance().getTeamHandler().recachePlayerTeams();
            RedisSaveTask.save(sender, true);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not Restart teams! Check console for errors.");
        }
        Main.getInstance().getEventHandler().getEvents().clear();
        Main.getInstance().getEventHandler().saveEvents();
        Main.getInstance().clearPersistance();
        final DB coll = Main.getInstance().getMongoPool().getDB(Main.DATABASE_NAME);
        coll.dropDatabase();
        for (final Team teams : Main.getInstance().getTeamHandler().getTeams()) {
            teams.disband();
        }
        sender.sendMessage(CC.translate("&a&lAll data has been cleared"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
    }
}