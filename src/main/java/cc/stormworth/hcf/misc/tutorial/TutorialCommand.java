package cc.stormworth.hcf.misc.tutorial;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.entity.Player;

public class TutorialCommand {

    @Command(names = {"tutorial"}, permission = "")
    public static void tutorial(Player player){
        player.sendMessage(CC.translate("&a https://youtu.be/Hj5jr2jSen0"));
    }

}
