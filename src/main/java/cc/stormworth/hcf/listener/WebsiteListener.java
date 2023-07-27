package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.general.LocationUtil;
import cc.stormworth.core.util.gson.serialization.ItemStackSerializer;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.UUID;

public class WebsiteListener {

    public WebsiteListener() {
        DBCollection mongoCollection = Main.getInstance().getMongoPool().getDB(Main.DATABASE_NAME).getCollection("Deaths");
        mongoCollection.createIndex(new BasicDBObject("uuid", 1));
    }

    public static void saveDeath(UUID victim, UUID killer, BasicDBObject inventory, String deathMessage, ItemStack killertool, Location location) {
        final BasicDBObject playerDeath = new BasicDBObject();

        playerDeath.put("_id", UUID.randomUUID().toString().substring(0, 7));

        playerDeath.append("killerUUID", killer != null ? killer.toString().replace("-", "") : null);

        playerDeath.append("playerInventory", inventory);
        playerDeath.append("uuid", victim.toString().replace("-", ""));

        Team team = Main.getInstance().getTeamHandler().getTeam(victim);

        playerDeath.append("location", LocationUtil.parseLocation(location));
        playerDeath.append("tool", killertool != null ? ItemStackSerializer.serialize(killertool) : null);
        playerDeath.append("message", deathMessage);
        playerDeath.append("team", team != null ? team.getName() : null);
        playerDeath.append("dtrinfo", (team != null ? team.getDTRColor() + Team.DTR_FORMAT.format(team.getDTR()) + ChatColor.YELLOW + " -> " + team.getDTRColor() + Team.DTR_FORMAT.format(team.getDTR() - 1) + team.getDTRSuffix() : null));
        playerDeath.append("date", new Date());

        new BukkitRunnable() {
            public void run() {
                Main.getInstance().getMongoPool().getDB(Main.DATABASE_NAME).getCollection("Deaths").save(playerDeath);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}