package cc.stormworth.hcf.profile.editor.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.editor.SelectLadderKitMenu;
import org.bukkit.entity.Player;

public class KitEditCommand {

    @Command(names = "kit edit", permission = "")
    public static void edit(Player player){
        new SelectLadderKitMenu().openMenu(player);
    }

}
