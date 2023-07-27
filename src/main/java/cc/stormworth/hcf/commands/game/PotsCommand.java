package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.brewingstand.menu.BuyMachineMenu;
import org.bukkit.entity.Player;

public class PotsCommand {

    @Command(names = "pots", permission = "")
    public static void pots(Player player){
        new BuyMachineMenu().openMenu(player);
    }

}
