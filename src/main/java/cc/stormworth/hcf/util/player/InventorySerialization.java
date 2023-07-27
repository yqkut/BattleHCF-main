package cc.stormworth.hcf.util.player;

import cc.stormworth.core.CorePlugin;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import net.minecraft.util.org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InventorySerialization {

    private static final Type TYPE = new TypeToken<ItemStack[]>() {
    }.getType();

    public static BasicDBObject serialize(ItemStack[] armor, ItemStack[] inventory) {
        BasicDBList armorDBObject = serialize(armor);
        BasicDBList inventoryDBObject = serialize(inventory);

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("ArmorContents", armorDBObject);
        dbObject.put("InventoryContents", inventoryDBObject);

        return dbObject;
    }

    public static BasicDBList serialize(ItemStack[] items) {
        List<ItemStack> kits = new ArrayList<>(Arrays.asList(items));
        kits.removeIf(Objects::isNull);
        return (BasicDBList) JSON.parse(CorePlugin.PLAIN_GSON.toJson(kits.toArray(new ItemStack[kits.size()])));
    }

    public static ItemStack[] deserialize(BasicDBList dbList) {
        return CorePlugin.PLAIN_GSON.fromJson(CorePlugin.PLAIN_GSON.toJson(dbList), TYPE);
    }

    public static String itemStackArrayToBase64(final ItemStack[] items) throws IllegalStateException {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (final ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(final String data) throws IOException {
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            final ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; ++i) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}