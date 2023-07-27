package cc.stormworth.hcf.shop.buttons;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.shop.ShopUtils;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class SellItemButton extends Button {

  final String name;
  final int cost;
  final int amount;
  final ItemStack itemStack;

  @Override
  public String getName(Player player) {
    return ChatColor.GOLD + name;
  }

  @Override
  public List<String> getDescription(Player player) {
    return Lists.newArrayList(
        "&7Sell: &c$" + NumberUtils.addComma(cost),
        "",
        "&fRight &eClick to sell " + amount + " block",
        "&fLeft &eClick to sell one block",
        "&cShift right &eClick to sell all blocks"
    );
  }

  @Override
  public ItemStack getButtonItem(Player player) {
    return new ItemBuilder(itemStack).name(getName(player))
        .setLore(getDescription(player)).amount(amount).build();
  }

  @Override
  public Material getMaterial(Player player) {
    return itemStack.getType();
  }

  @Override
  public void clicked(Player player, int slot, ClickType clickType) {

    if (clickType == ClickType.SHIFT_RIGHT) {
      Material material = itemStack.getType();

      int price = 0;

      int itemsSold = 0;

      List<ItemStack> toSellList = new ArrayList<>();

      if (player.getInventory().all(material).size() > 0) {
        HashMap<Integer, ? extends ItemStack> map = player.getInventory().all(material);

        for (ItemStack stack : map.values()) {
          price += ShopUtils.getPrice(stack, amount);
          itemsSold += stack.getAmount();
          toSellList.add(stack);
        }
      }

      if (price == 0) {
        player.sendMessage(ChatColor.RED + "You don't have anything to sell!");
        Button.playFail(player);
        return;
      }

      HCFProfile.get(player).getEconomyData().addBalance(price);

      for (ItemStack item : toSellList) {
        player.getInventory().removeItem(item);
      }

      player.sendMessage(
          CC.translate("&aYou sold &e" + itemsSold + " &aitems for &e$" + price + ""));
      Button.playSuccess(player);
    } else if (clickType == ClickType.RIGHT) {
      if (ShopUtils.containAmount(player, itemStack, amount)) {
        ShopUtils.removeItem(player, itemStack, amount);
        player.updateInventory();
        player.sendMessage(ChatColor.GREEN + "Sold " + amount + " " + itemStack.getType().toString().toLowerCase().replace("_", " ") + " for $" + cost);

        HCFProfile.get(player).getEconomyData().addBalance(cost);

        Button.playSuccess(player);
      } else {
        player.sendMessage(
            ChatColor.RED + "You do not have enough " + itemStack.getType().toString().toLowerCase()
                .replace("_", " ") + " to sell.");
        Button.playFail(player);
      }
    } else {
      if (ShopUtils.containAmount(player, itemStack, 1)) {
        int newCost = cost / amount;
        ShopUtils.removeItem(player, itemStack, 1);
        player.updateInventory();
        player.sendMessage(
            ChatColor.GREEN + "Sold 1 " + itemStack.getType().toString().toLowerCase()
                .replace("_", " ") + " for $" + newCost);

        HCFProfile.get(player).getEconomyData().addBalance(newCost);

        Button.playSuccess(player);
      } else {
        player.sendMessage(
            ChatColor.RED + "You do not have enough " + itemStack.getType().toString().toLowerCase()
                .replace("_", " ") + " to sell.");
        Button.playFail(player);
      }
    }
  }
}