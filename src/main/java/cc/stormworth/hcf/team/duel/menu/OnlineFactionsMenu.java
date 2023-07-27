package cc.stormworth.hcf.team.duel.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.hcf.providers.tab.TabProvider;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class OnlineFactionsMenu extends Menu {

    private final Team team;

    @Override
    public String getTitle(Player player) {
        return "Select a faction to duel";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> buttons = Maps.newHashMap();


        TabProvider.getCachedTeamOnlineList().keySet().forEach(teamOnline -> {

        });


        return buttons;
    }

    @RequiredArgsConstructor
    public class OnlineFactionButton extends Button {
        private final Team team;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER).name("&6" + team.getName())
                    .addToLore(
                            "&7Members: &a" + team.getMembers().size(),
                            "&7Kills: &a" + team.getKills(),
                            "&7Deaths: &a" + team.getDeaths(),
                            "",
                            "&eClick to duel"
                    )
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

        }
    }
}
