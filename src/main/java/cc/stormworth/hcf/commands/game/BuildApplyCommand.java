package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.entity.Player;

public class BuildApplyCommand {


    @Command(names = "buildapply", permission = "")
    public static void buildapply(Player player){
        player.sendMessage(CC.translate("&eTo Apply for a Build tournament, enter to:&6 https://bit.ly/3yQCZXP"));
    }

}
