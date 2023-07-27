package cc.stormworth.hcf.comunitychest;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Setter
@Getter
public class ComunityChest {

  private final MongoCollection<Document> collection = Main.getInstance().getMongoPool()
      .getDatabase(Main.DATABASE_NAME).getCollection("comunityChest");

  private ItemStack[] items = new ItemStack[54];
  private Inventory inventory = Bukkit.createInventory(null, 9 * 6,
      CC.translate("&6&lComunity Chest"));

  public ComunityChest() {
    load();
    for (int i = 0; i < items.length; i++) {
      inventory.setItem(i, items[i]);
    }
  }

  public void load() {
    Document document = collection.find(new Document("_id", 1)).first();

    if (document == null) {
      return;
    }

    List<String> items = document.getList("items", String.class);

    for (int i = 0; i < items.size(); i++) {
      this.items[i] = CorePlugin.GSON.fromJson(items.get(i), ItemStack.class);
    }
  }

  public void save() {
    Document document = new Document();
    List<String> items = Lists.newArrayList();

    for (ItemStack item : this.inventory.getContents()) {
      if (item == null) {
        item = new ItemStack(Material.AIR);
      }

      items.add(CorePlugin.GSON.toJson(item));
    }

    document.append("items", items);

    collection.replaceOne(new Document("_id", 1), document, new ReplaceOptions().upsert(true));
  }

}