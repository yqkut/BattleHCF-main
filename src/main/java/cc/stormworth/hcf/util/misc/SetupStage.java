package cc.stormworth.hcf.util.misc;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum SetupStage {

    NONE("None", 0, null, null),
    SETSPAWN("Set Spawn", 1, "setworldspawn", ItemBuilder.of(Material.WATCH).name("&aSet Spawn Location").build());

    @Getter
    public static ItemStack backbutton = ItemBuilder.of(Material.INK_SACK).data((short) 1).build();
    @Getter
    public static SetupStage stage = SetupStage.NONE;
    @Getter
    @Setter
    public static boolean setupmode = false;
    private String name;
    private int id;
    private String command;
    private ItemStack itemStack;

    public static void setStage(SetupStage newstage) {
        stage = newstage;
        giveAll();
    }

    public static void setStage(int newstage) {
        stage = values()[newstage];
        giveAll();
    }

    public static void giveAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                giveItem(player);
            }
        }
    }

    public static void giveItem(Player player) {
        for (SetupStage stage : SetupStage.values()) {
            player.getInventory().removeItem(stage.getItemStack());
        }
        player.getInventory().setItem(0, getStage().getItemStack());
        player.getInventory().setItem(8, getBackbutton());
        player.sendMessage(CC.YELLOW + "The setup mode is in the " + stage.name + " stage.");
    }
}