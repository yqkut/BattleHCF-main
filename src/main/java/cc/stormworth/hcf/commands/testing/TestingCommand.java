package cc.stormworth.hcf.commands.testing;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.player.BookUtils;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TestingCommand {

    @Command(names = "changelogs", permission = "OWNER")
    public static void book(Player player){

        ItemStack book = BookUtils.book("&9&lUPDATES &7(Changelogs)", "Battle",
                CC.translate("&9&lUPDATES &7(Changelogs)" +
                        " \n" +
                        "&7➟&0 Enderpearls &a(Updated)\n"+
                        "&7➟&0 Classes Effects remove from miliseconds &7(Fixed)\n"+
                        "&7➟&0 Shop brewing &7(Fixed)\n"+
                        "&7➟&0 Giftbox rewards per key &b(Increased)\n" +
                        "&7➟&0 x2 Rank Sale on SOTW Timer &a(Added)\n"),
                CC.translate("&7➟&0 x2-5 Airdrops/Gems Sale on SOTW Timer &a(Added)\n" +
                        "&7➟&0 Dupplying abilities with abilities with Anvil &7(Fixed)\n" +
                        "&7➟&0 1.7, chansing KB &7(Fixed) \n" +
                        "&7➟&0 Fishing Rod, chasing KB &a(Updated) \n" +
                        "&7➟&0 Blinding ability &4(Removed) \n"),
                CC.translate("&7➟&0 Dash ability &4(Removed) \n" + "&7➟&0 Bow Teleporter ability &4(Removed) \n"+ "&7➟&0 Second Change ability &4(Maintenance)")
        );

        BookUtils.openBook(book, player);
    }


    @Command(names = "enderpearlconfig reload", permission = "op")
    public static void deep(Player player){
        Main.getInstance().getEnderPearlSettings().reloadConfig();

        player.sendMessage(CC.translate("&aConfig reloaded"));
    }


    public static ItemStack setNBTTag() {
        ItemStack is = new ItemStack(Material.DIAMOND_BOOTS, 1);
        net.minecraft.server.v1_7_R4.ItemStack nmsis = CraftItemStack.asNMSCopy(is);
        NBTTagCompound bd = nmsis.hasTag() ? nmsis.getTag() : new NBTTagCompound();

        bd.setInt("deep", 1);

        nmsis.setTag(bd);

        is = CraftItemStack.asBukkitCopy(nmsis);

        return is;
    }

    @Command(names = "hasdeeptest", permission = "op")
    public static void hasdeep(Player player) {

        ItemStack hand = player.getItemInHand();
        net.minecraft.server.v1_7_R4.ItemStack nmsApple = CraftItemStack.asNMSCopy(hand);
        NBTTagCompound applecompound = (nmsApple.hasTag()) ? nmsApple.getTag() : new NBTTagCompound();

        if (applecompound.hasKey("deep")) {
            player.sendMessage("Has depth strider: " + applecompound.getInt("deep"));
        } else {
            player.sendMessage("No depth strider");
        }
    }

}