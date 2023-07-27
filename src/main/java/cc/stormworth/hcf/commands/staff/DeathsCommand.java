package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.deathmessage.util.deaths.DeathsMenu;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DeathsCommand {

    public final static DateFormat FORMAT = new SimpleDateFormat("M dd yyyy h:mm a");

    @Command(names = {"deaths"}, permission = "MOD", async = true)
    public static void deaths(Player sender, @Param(name = "player") UUID player) {
        sender.sendMessage(CC.YELLOW + "Loading deaths...");
        new DeathsMenu(player).openMenu(sender);
    }

    @Command(names = {"deathrefund"}, permission = "MODPLUS", async = true)
    public static void refund(Player sender, @Param(name = "id") String id) {
        DBCollection mongoCollection = Main.getInstance().getMongoPool().getDB(Main.DATABASE_NAME).getCollection("Deaths");
        DBObject object = mongoCollection.findOne(id);

        if (object != null) {
            BasicDBObject basicDBObject = (BasicDBObject) object;
            Player player = Bukkit.getPlayer(UUIDfromString(object.get("uuid").toString()));

            if (basicDBObject.containsKey("refundedBy")) {
                sender.sendMessage(ChatColor.RED + "This death was already refunded by " + UUIDUtils.name(UUIDfromString(basicDBObject.getString("refundedBy"))) + ".");
                return;
            }

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player isn't on to receive items.");
                return;
            }

            ItemStack[] contents = CorePlugin.PLAIN_GSON.fromJson(JSON.serialize(((BasicDBObject) basicDBObject.get("playerInventory")).get("contents")), ItemStack[].class);
            ItemStack[] armor = CorePlugin.PLAIN_GSON.fromJson(JSON.serialize(((BasicDBObject) basicDBObject.get("playerInventory")).get("armor")), ItemStack[].class);

            LastInvCommand.cleanLoot(contents);
            LastInvCommand.cleanLoot(armor);

            player.getInventory().setContents(contents);
            player.getInventory().setArmorContents(armor);

            basicDBObject.put("refundedBy", sender.getUniqueId().toString().replace("-", ""));
            basicDBObject.put("refundedAt", new Date());

            mongoCollection.save(basicDBObject);

            player.sendMessage(ChatColor.GREEN + "Your inventory has been reset to an inventory from a previous life.");
            sender.sendMessage(ChatColor.GREEN + "Successfully refunded inventory to " + player.getName() + ".");

        } else {
            sender.sendMessage(ChatColor.RED + "Death not found.");
        }
    }

    public static UUID UUIDfromString(String string) {
        return UUID.fromString(
                string.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                )
        );
    }
}