package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class HelpCommand {

    private static final String[] help = new String[]{
            "",
            "&6&lMap Infomation&7:",
            "&7Current Map&8 - &fMap 9",
            "&7MaBorder&8 - &f" + CorePlugin.getInstance().getConfigFile().getConfig().getInt("borders.world"),
            "&7Map Kit&8 - &fP" + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + " S" + Enchantment.DAMAGE_ALL.getMaxLevel(),
            "",
            "&6&lUseful Commands&7:",
            "&7/teamspeak - &fProvides the Teamspeak Server IP",
            "&7/report <Player> <Reason> - &fA command to report rule-breakers",
            "&7/helpop <Message> - &fAlerts online Staff Members that you are in need of assistance",
            "",
            "&6&lOther infomation&7:",
            "&7Website&8 - &fwww.battle.rip",
            "&7Buycraft&8 - &fstore.battle.rip",
            "&7Teamspeak&8 - &fts.battle.rip",
            "",
    };
    
    @Command(names = {"help"}, permission = "")
    public static void help(Player player){

        for(String s : help){
            player.sendMessage(CC.translate(s));
        }
    }

}
