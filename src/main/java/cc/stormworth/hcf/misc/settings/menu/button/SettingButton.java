package cc.stormworth.hcf.misc.settings.menu.button;

import cc.stormworth.core.menu.Button;
import cc.stormworth.hcf.misc.settings.Setting;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class SettingButton extends Button {

    private final Setting setting;

    public SettingButton(final Setting setting) {
        this.setting = setting;
    }

    @Override
    public String getName(Player player) {
        return this.setting.getName();
    }

    @Override
    public List<String> getDescription(final Player player) {
        final List<String> description = Lists.newArrayList();
        description.add("");
        description.addAll(this.setting.getDescription());
        description.add("");
        if (setting.isEnabled(player)) {
            description.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "  ► " + setting.getEnabledText());
            description.add("    " + setting.getDisabledText());
        } else {
            description.add("    " + setting.getEnabledText());
            description.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "  ► " + setting.getDisabledText());
        }
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return this.setting.getIcon();
    }

    public void clicked(Player player, int slot, ClickType clickType) {
        this.setting.toggle(player);
    }
}