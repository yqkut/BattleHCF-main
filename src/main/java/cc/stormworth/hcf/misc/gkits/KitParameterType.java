package cc.stormworth.hcf.misc.gkits;

import cc.stormworth.core.util.command.param.ParameterType;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KitParameterType implements ParameterType<Kit> {

    public Kit transform(CommandSender sender, String source) {
        for (final Kit kit : Main.getInstance().getKitManager().getKits()) {
            if (source.equalsIgnoreCase(kit.getName())) {
                return (kit);
            }
        }
        sender.sendMessage(ChatColor.RED + "There is not an kit named " + source + ".");
        return (null);
    }

}