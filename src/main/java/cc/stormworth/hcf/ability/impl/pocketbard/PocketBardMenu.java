package cc.stormworth.hcf.ability.impl.pocketbard;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.general.GlowGlassButton;
import cc.stormworth.hcf.ability.Ability;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class PocketBardMenu extends Menu {

  int[] glassslots = new int[]{0, 1, 7, 8, 9, 17, 18, 19, 25, 26};

  public PocketBardMenu() {
    this.setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return "Pocket Bard";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    for (int slot : glassslots) {
      buttons.put(slot, new GlowGlassButton((byte) 1));
    }

    buttons.put(11, new PocketBardButton("&6Strength II", Ability.getByName("Strength")));
    buttons.put(12, new PocketBardButton("&6Speed III", Ability.getByName("Speed")));
    buttons.put(13, new PocketBardButton("&6Resistance III", Ability.getByName("Resistance")));
    buttons.put(14, new PocketBardButton("&6Regeneration III", Ability.getByName("Regeneration")));
    buttons.put(15, new PocketBardButton("&6Jump Boost VII", Ability.getByName("JumpBoost")));

    return buttons;
  }
}