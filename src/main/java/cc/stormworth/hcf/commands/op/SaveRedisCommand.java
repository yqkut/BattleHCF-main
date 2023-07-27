package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.persist.RedisSaveTask;
import org.bukkit.command.CommandSender;

public class SaveRedisCommand {
    @Command(names = {"SaveData"}, hidden = true, permission = "op")
    public static void saveRedis(final CommandSender sender) {
        RedisSaveTask.save(sender, false);
        Main.getInstance().saveData();
        sender.sendMessage(CC.translate("&aSuccessfully Saved."));
    }

    @Command(names = {"SaveData ForceAll"}, hidden = true, permission = "op")
    public static void saveRedisForceAll(final CommandSender sender) {
        RedisSaveTask.save(sender, true);
        Main.getInstance().saveData();
        sender.sendMessage(CC.translate("&aSuccessfully Saved. &c(Forced)"));
    }
}