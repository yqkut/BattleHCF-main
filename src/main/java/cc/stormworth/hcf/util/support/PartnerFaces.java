package cc.stormworth.hcf.util.support;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.FaceEmotes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public enum PartnerFaces {

  Dudas("bff40781-2302-400f-b2b9-2f66e1b15ec5", null, 12),
  NicoArg17("6cefcc87-cd15-4eb7-be0e-c1dafb9fe8f3", null, 12),
  Jaarabe("c2eb5cba-cb25-4f2d-bc04-93c2641fccd5", null, 12),
  Elivann("3c904d71-45b3-40c8-9b79-7be69c1144a3", null, 12),
  Houp("cbc6e6a4-eb17-4afe-a84d-a2e9634b4165", null, 12),
  Frozeado("d9ca9c25-a0d4-410e-ad09-fd8011c8d356", null, 13),
  TobiiM9("a13872f6-964b-4d9d-9547-a99ee1aeba6f", null, 12),
  Eamotyn("0f3e1d57-ea64-41e4-8081-f4168e2e6fa0", null, 12),
  Luctrix_("bb12c16e-8c9c-494a-a16c-0e24c82fb864", null, 12),
  DaniRamos("723b6be1-a1d7-45e2-8cec-f2e41d06b854", null, 12);


  @Setter
  @Getter
  private String uuid;
  @Setter
  @Getter
  private ItemStack item;
  @Setter
  @Getter
  private int slot;

  public static ItemStack createSkull(String name, int amount, UUID skullowner, List<String> lore) {
    ItemStack item = FaceEmotes.createSkull(FaceEmotes.getFromUUID(skullowner)[0]);
    ItemMeta meta = item.getItemMeta();
    item.setAmount(amount);
    meta.setDisplayName(CC.translate(name));
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  public String getName() {
    return CC.translate(this.name());
  }
}