package cc.stormworth.hcf.server.support;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.support.PartnerFaces;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class SupportButton extends Button {

  boolean adminview;
  private final PartnerFaces partner;
  private final String playerName;

  public SupportButton(final PartnerFaces partner, final boolean b) {
    this.adminview = false;
    this.partner = partner;
    this.adminview = b;
    this.playerName = partner.getName();
  }

  @Override
  public String getName(Player player) {
    return null;
  }

  @Override
  public List<String> getDescription(Player player) {
    return null;
  }

  @Override
  public Material getMaterial(Player player) {
    return null;
  }

  @Override
  public ItemStack getButtonItem(Player player) {
    final List<String> description = Lists.newArrayList();
    if (this.adminview) {
      description.add(CC.translate("&c&m-----------------------------------"));
      description.add(CC.translate(
          "&e" + playerName + " &6- " + Main.getInstance().getCreatorsCountMap().getVotes(UUID.fromString(partner.getUuid())) + " Votes."));
      description.add(CC.translate("&c&m-----------------------------------"));
    } else {
      description.add(CC.translate("&6&l⭐ &eYou will receive &7&ox3 &d&lPartner &dkeys!"));
      description.add(CC.translate(""));
      description.add(CC.translate("&eClick to vote"));
    }
    ItemStack item = partner.getItem().clone();
    ItemMeta meta = item.getItemMeta().clone();
    meta.setDisplayName(ChatColor.GOLD + this.playerName);
    meta.setLore(description);
    item.setItemMeta(meta);
    return item;
  }

  @Override
  public void clicked(Player player, int slot, ClickType clickType) {
    if (this.adminview) {
      return;
    }
    if (Main.getInstance().getSupportedMap()
        .hasSupported(player.getAddress().getAddress().getHostAddress()) || Main.getInstance()
        .getSupportedMap().hasSupported(player.getName())) {
      playFail(player);
      player.closeInventory();
      player.sendMessage(CC.translate("&cSorry, but you can only vote once per map!"));
      return;
    }
    player.closeInventory();
    Main.getInstance().getSupportedMap()
        .setSupported(player.getAddress().getAddress().getHostAddress(), true);
    Main.getInstance().getSupportedMap().setSupported(player.getName(), true);
    Main.getInstance().getCreatorsCountMap().addVote(UUID.fromString(partner.getUuid()));

    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 6");
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "voucher give " + playerName + " " + player.getName() + " 1");

    player.sendMessage(CC.translate(""));
    player.sendMessage(CC.translate(" &eYou have supported &6&l" + playerName));
    player.sendMessage(CC.translate(" &eYou have received &7&ox6 &b&lPartner &dkeys &eand &6&l" + playerName+" &egKit!"));
    player.sendMessage(CC.translate(""));
    playSuccess(player);

    new TitleBuilder(
            "&a&lCongratulations!",
                    "&7&oYou have successfully voted!",
                    5,
                    40,
                    5
  ).send(player);

  player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
}}