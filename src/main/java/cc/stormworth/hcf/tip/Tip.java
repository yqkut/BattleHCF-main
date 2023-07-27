package cc.stormworth.hcf.tip;

import cc.stormworth.core.kt.util.ItemBuilder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class Tip {

  private boolean enable;

  private String name;

  private String name_english;
  private String name_spanish;

  private List<String> lore_english;
  private List<String> lore_spanish;

  private List<String> messages_english;
  private List<String> messages_spanish;
  
  public ItemStack getEnglishItem() {
    return new ItemBuilder(Material.PAPER).name(name_english).setLore(lore_english).build();
  }

  public ItemStack getSpanishItem() {
    return new ItemBuilder(Material.PAPER).name(name_spanish).setLore(lore_spanish).build();
  }
}