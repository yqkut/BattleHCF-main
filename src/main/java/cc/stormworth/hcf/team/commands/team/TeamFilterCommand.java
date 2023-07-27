package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.team.utils.FilterMenu;
import org.bukkit.entity.Player;

public class TeamFilterCommand {

    @Command(names = {"team filter", "t filter", "f filter", "faction filter", "fac filter"}, permission = "")
    public static void settings(final Player sender) {
        new FilterMenu().openMenu(sender);
    }
}