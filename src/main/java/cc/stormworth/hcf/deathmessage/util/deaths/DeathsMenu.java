package cc.stormworth.hcf.deathmessage.util.deaths;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.pagination.PaginatedMenu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.LocationUtil;
import cc.stormworth.core.util.gson.serialization.ItemStackSerializer;
import cc.stormworth.core.util.time.DateUtil;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.DeathsCommand;
import cc.stormworth.hcf.commands.staff.LastInvCommand;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import com.mongodb.util.JSON;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@AllArgsConstructor
public class DeathsMenu extends PaginatedMenu {

  private UUID target;

  @Override
  public boolean isUpdateAfterClick() {
    return false;
  }

  @Override
  public String getPrePaginatedTitle(Player player) {
    return CC.YELLOW + UUIDUtils.name(target) + "'s Deaths";
  }

  @Override
  public Map<Integer, Button> getAllPagesButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    DBCollection mongoCollection = Main.getInstance().getMongoPool().getDB(Main.DATABASE_NAME)
        .getCollection("Deaths");

    for (DBObject object : mongoCollection.find(
        new BasicDBObject("uuid", target.toString().replace("-", "")),
        new DBCollectionFindOptions().limit(10).sort(new BasicDBObject("date", -1)))) {
      BasicDBObject basicDBObject = (BasicDBObject) object;

      ItemStack[] contents = CorePlugin.PLAIN_GSON.fromJson(
          JSON.serialize(((BasicDBObject) basicDBObject.get("playerInventory")).get("contents")),
          ItemStack[].class);
      ItemStack[] armor = CorePlugin.PLAIN_GSON.fromJson(
          JSON.serialize(((BasicDBObject) basicDBObject.get("playerInventory")).get("armor")),
          ItemStack[].class);
      PotionEffect[] effects = CorePlugin.PLAIN_GSON.fromJson(
          JSON.serialize(((BasicDBObject) basicDBObject.get("playerInventory")).get("effects")),
          PotionEffect[].class);

      LastInvCommand.cleanLoot(contents);
      LastInvCommand.cleanLoot(armor);

      Location location = LocationUtil.convertLocation(object.get("location").toString());
      ItemStack tool = ItemStackSerializer.deserialize(((BasicDBObject) basicDBObject.get("tool")));

      Calendar from = Calendar.getInstance();
      Calendar to = Calendar.getInstance();
      from.setTime(basicDBObject.getDate("date"));
      to.setTime(new Date(System.currentTimeMillis()));

      DeathInfo deathInfo = new DeathInfo(object.get("_id").toString(),
          DeathsCommand.UUIDfromString(object.get("uuid").toString()),
          object.get("killerUUID") != null ? DeathsCommand.UUIDfromString(
              object.get("killerUUID").toString()) : null,
          contents,
          armor,
          effects,
          location,
          tool,
          object.get("message").toString(),
          object.get("team") != null ? object.get("team").toString() : null,
          object.get("dtrinfo") != null ? object.get("dtrinfo").toString() : null,
              DateUtil.formatDateDiff(from, to) + " ago");
      buttons.put(buttons.size(), new DeathButton(deathInfo));
    }

    return buttons;
  }

  @Override
  public int getMaxItemsPerPage(Player player) {
    return 27;
  }
}