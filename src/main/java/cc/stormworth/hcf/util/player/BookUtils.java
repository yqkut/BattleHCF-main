package cc.stormworth.hcf.util.player;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;
import net.minecraft.server.v1_7_R4.NBTTagString;
import net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class BookUtils {

    public ItemStack book(String title, String author, String... pages) {
        ItemStack is = new ItemStack(Material.WRITTEN_BOOK, 1);
        net.minecraft.server.v1_7_R4.ItemStack nmsis = CraftItemStack.asNMSCopy(is);
        NBTTagCompound bd = new NBTTagCompound();
        bd.setString("title", title);
        bd.setString("author", author);
        NBTTagList bp = new NBTTagList();

        for(String text : pages) {
            bp.add(new NBTTagString(text));
        }

        bd.set("pages", bp);
        nmsis.setTag(bd);
        is = CraftItemStack.asBukkitCopy(nmsis);
        return is;
    }

    public void openBook(ItemStack book, Player player) {
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);
        //player.updateInventory();

        TaskUtil.runLater(Main.getInstance(), () -> {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", new byte[0]));
            player.getInventory().setItem(slot, old);
        }, 3L);
    }

}
