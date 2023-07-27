package cc.stormworth.hcf.misc.crazyenchants.processors;

import cc.stormworth.core.util.command.param.ParameterType;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.CEnchantment;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CEnchantmentType implements ParameterType<CEnchantment> {

  public CEnchantment transform(final CommandSender sender, final String source) {
    for (CEnchantment enchant : Main.getInstance().getEnchantmentsManager()
        .getRegisteredEnchantments()) {
      if (source.equalsIgnoreCase(enchant.getName())) {
        return enchant;
      }
    }
    sender.sendMessage(ChatColor.RED + "There is not a CEnchantment named " + source + ".");
    return null;
  }
}