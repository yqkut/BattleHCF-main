package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.entity.Player;

public class PartnerCommands {

    @Command(names = "setmaxhealth", permission = "PARTNER")
    public static void setMaxHealth(Player player, @Param(name = "health") int health) {
        player.setMaxHealth(health);
        player.setHealth(health);
        player.sendMessage(CC.translate("&eYour max health has been set to &6" + health));
    }


    @Command(names = "togglerandomeffects", permission = "PARTNER")
    public static void togglerandomeffects(Player player) {
        HCFProfile profile = HCFProfile.get(player);

        profile.setRandomEffects(!profile.isRandomEffects());

        player.sendMessage(CC.translate("&eYour random effects have been toggled to &6" + (profile.isRandomEffects() ? "&aenabled" : "&cdisabled")));
    }

}
