package cc.stormworth.hcf.profile;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class PrivateChest {

  private final int number;
  private final List<ItemStack> contents = Lists.newArrayList();

  public PrivateChest(Document document) {
    List<String> noreclaimeditems = document.getList("contents", String.class);
    for (String item : noreclaimeditems) {
      ItemStack itemStack = CorePlugin.GSON.fromJson(item, ItemStack.class);
      this.contents.add(itemStack);
    }
    this.number = document.getInteger("number", 0);
  }

  public Document serialize() {
    Document document = new Document();

    document.put("contents",
        contents.stream().map(CorePlugin.GSON::toJson).collect(Collectors.toList()));

    document.put("number", number);
    return document;
  }

  public Inventory getInventory() {
    Inventory inventory = Bukkit.createInventory(null, 54,
        CC.translate("&ePrivate Chest " + number));

    for (int i = 0; i < 54; i++) {
      if (contents.size() > i) {
        inventory.setItem(i, contents.get(i));
      }
    }

    return inventory;
  }

}