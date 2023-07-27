package cc.stormworth.hcf.misc.settings.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.misc.settings.menu.SettingsMenu;
import org.bukkit.entity.Player;

public class SettingsCommand {

    @Command(names = {"settings", "options"}, permission = "")
    public static void settings(final Player sender) {
        new SettingsMenu().openMenu(sender);
    }
}