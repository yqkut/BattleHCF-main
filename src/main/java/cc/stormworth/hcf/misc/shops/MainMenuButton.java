package cc.stormworth.hcf.misc.shops;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class MainMenuButton extends Button {

    String name;
    Material mat;
    Menu menu;
    List<String> lore;

    public MainMenuButton(String name, Material mat, Menu menu, List<String> lore) {
        this.name = name;
        this.mat = mat;
        this.menu = menu;
        this.lore = lore;
    }

    @Override
    public String getName(Player player) {
        return name;
    }

    @Override
    public List<String> getDescription(Player player) {
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return mat;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        playNeutral(player);
        menu.openMenu(player);
    }
}