package cc.stormworth.hcf.team.dtr;

import cc.stormworth.core.util.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class DTRBitmaskType implements ParameterType<DTRBitmask> {
    public DTRBitmask transform(final CommandSender sender, final String source) {
        for (final DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (source.equalsIgnoreCase(bitmaskType.getName())) {
                return bitmaskType;
            }
        }
        sender.sendMessage(ChatColor.RED + "No bitmask type with the name " + source + " found.");
        return null;
    }

}