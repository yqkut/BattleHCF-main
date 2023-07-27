package cc.stormworth.hcf.shop.buttons;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.economy.EconomyData;
import cc.stormworth.hcf.util.number.NumberUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor
public class BuyItemButton extends Button {

  private final String name;
  private final int cost;
  private final int amount;
  private final ItemStack itemStack;
  private ItemStack buyItemStack;

  public BuyItemButton(String name, int cost, int amount, ItemStack itemStack,
      ItemStack buyItemStack) {
    this.name = name;
    this.cost = cost;
    this.amount = amount;
    this.itemStack = itemStack;
    this.buyItemStack = buyItemStack;
  }

  @Override
  public String getName(Player player) {
    return ChatColor.GOLD + name;
  }

  @Override
  public List<String> getDescription(Player player) {
    return Lists.newArrayList(
        "&7Buy: &a$" + NumberUtils.addComma(cost),
        "",
        "&eClick to buy!"
    );
  }

  @Override
  public byte getDamageValue(Player player) {
    return (byte) itemStack.getDurability();
  }

  @Override
  public ItemStack getButtonItem(Player player) {
    return new ItemBuilder(itemStack.clone()).name(getName(player))
        .setLore(getDescription(player)).amount(amount).build();
  }

  @Override
  public Material getMaterial(Player player) {
    return itemStack.getType();
  }

  @Override
  public void clicked(Player player, int slot, ClickType clickType) {
    if (player.getInventory().firstEmpty() == -1) {
      player.sendMessage(ChatColor.RED + "You don't have enough space in your inventory!");
      Button.playFail(player);
      return;
    }

    HCFProfile profile = HCFProfile.get(player);

    EconomyData economyData = profile.getEconomyData();

    if (economyData.getBalance() < cost) {
      player.sendMessage(ChatColor.RED + "You don't have enough money!");
      Button.playFail(player);
      return;
    }

    ItemStack item;

    if (buyItemStack == null) {
      item = itemStack.clone();
    } else {
      item = buyItemStack.clone();
    }

    item.setAmount(amount);

    economyData.subtractBalance(cost);

    player.getInventory().addItem(item);

    player.sendMessage(ChatColor.GREEN + "You have bought " + amount + " " + name + " for $"
        + NumberUtils.addComma(cost));
    Button.playSuccess(player);
  }
}