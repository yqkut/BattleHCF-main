package cc.stormworth.hcf.misc.trade;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class TradeMenu extends Menu {

  private final Map<UUID, List<ItemStack>> itemsLeft = Maps.newHashMap();
  private final Map<UUID, List<ItemStack>> itemsRight = Maps.newHashMap();
  private final Player target;
  private final Player trader;

  private int countdown = 4;
  private BukkitRunnable runnable;

  private List<UUID> confirmed = Lists.newArrayList();

  public TradeMenu(Player target, Player trader) {
    this.target = target;
    this.trader = trader;

    openMenu(target);

    itemsLeft.put(trader.getUniqueId(), Lists.newArrayList());
    itemsRight.put(target.getUniqueId(), Lists.newArrayList());

    setAutoUpdate(true);
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 9 * 6;
  }

  @Override
  public String getTitle(Player player) {
    return "&6Trade with&e " + target.getName();
  }

  @Override
  public void onOpen(Player player) {
    setClosedByMenu(false);
  }

  @Override
  public void onClose(Player player) {

    if (isClosedByMenu()) {
      return;
    }

    if (getItemsByPlayer(trader) != null) {
      getItemsByPlayer(trader).forEach(item -> {
        if (item != null) {
          trader.getInventory().addItem(item);
        }
      });
    }

    if (getItemsByPlayer(target) != null) {
      getItemsByPlayer(target).forEach(item -> {
        if (item != null) {
          target.getInventory().addItem(item);
        }
      });
    }

    if (player == target) {
      setClosedByMenu(true);
      trader.closeInventory();
    } else {
      setClosedByMenu(true);
      target.closeInventory();
    }

    target.sendMessage(CC.translate("&cTrade has been canceled."));
    trader.sendMessage(CC.translate("&cTrade has been canceled."));

    confirmed.clear();
    itemsRight.clear();
    itemsLeft.clear();
    if (runnable != null) {
      runnable.cancel();
    }
  }

  public List<ItemStack> getItemsByPlayer(Player player) {
    if (itemsRight.containsKey(player.getUniqueId())) {
      return itemsRight.get(player.getUniqueId());
    }

    return itemsLeft.get(player.getUniqueId());
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (int i = 0; i < 9; i++) {
      if (runnable != null) {
        buttons.put(i,
            Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name(" ")
                .data((short) 5)
                .amount(countdown)
                .build()));
      } else {
        buttons.put(i, Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
            .name(" ")
            .data((short) 7)
            .build()));
      }
    }

    for (int i = 0; i < 6; i++) {
      if (runnable == null) {
        buttons.put(getSlot(4, i),
            Button.fromItem(
                new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .name(" ").data((short) 7)
                    .amount(1)
                    .build()));
      } else {
        buttons.put(getSlot(4, i),
            Button.fromItem(
                new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .name(" ").data((short) 5)
                    .amount(countdown)
                    .build()));
      }
    }

    HCFProfile traderProfiler = HCFProfile.get(trader);

    buttons.put(3, Button.fromItem(
            new ItemBuilder(Material.SKULL_ITEM).data((short) 3).setSkullOwner(trader.getName()).name("&a&l" + trader.getName())
                    .addToLore("",
                            CC.translate("&eBalance: &a" + traderProfiler.getEconomyData().getFormattedBalance())).build()));

    buttons.put(4, new Button() {
      @Override
      public String getName(Player player) {
        return "&aConfirmation!";
      }

      @Override
      public List<String> getDescription(Player player) {
        return Lists.newArrayList(
            "",
            "&7Both players have to confirm the trade.",
            "&7Once both have confirmed, there will be a 5s countdown.",
            "",
            "&eStatus&f: ",
            "",
            "&6&l• &e" + trader.getName() + "&7: " + (confirmed.contains(trader.getUniqueId())
                ? "&aConfirmed" : "&cPending..."),
            "&6&l• &e" + target.getName() + "&7: " + (confirmed.contains(target.getUniqueId())
                ? "&aConfirmed" : "&cPending..."),
            "",
            "&eClick to confirm!"
        );
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.EMPTY_MAP;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        if (confirmed.contains(player.getUniqueId())) {
          player.sendMessage(CC.translate("&cYou have already confirmed the trade!"));
          return;
        }

        confirmed.add(player.getUniqueId());

        setClosedByMenu(true);
        player.playSound(target.getLocation(), Sound.CLICK, 1, 1);
        if (confirmed.size() == 2) {
          runnable = new BukkitRunnable() {
            @Override
            public void run() {

              if (countdown == 1) {
                HCFProfile targetProfile = HCFProfile.get(target);
                HCFProfile traderProfile = HCFProfile.get(trader);

                itemsLeft.get(trader.getUniqueId()).forEach(item -> {
                  if (target.getInventory().firstEmpty() == -1) {
                    targetProfile.getNoReclaimedItems().add(item);
                  } else {
                    target.getInventory().addItem(item);
                  }
                });

                itemsRight.get(target.getUniqueId()).forEach(item -> {
                  if (trader.getInventory().firstEmpty() == -1) {
                    traderProfile.getNoReclaimedItems().add(item);
                  } else {
                    trader.getInventory().addItem(item);
                  }
                });

                if (targetProfile.getNoReclaimedItems().size() > 0) {
                  target.sendMessage("");
                  target.sendMessage(CC.translate("&a&eYour inventory was &cfull&e."));
                  Clickable clickable = new Clickable("&a[Click here] &eto recover the items.",
                      "&a[Click here]",
                      "/reclaimitems");
                  clickable.sendToPlayer(target);
                  target.sendMessage("");
                }

                if (traderProfile.getNoReclaimedItems().size() > 0) {
                  trader.sendMessage("");
                  trader.sendMessage(CC.translate("&a&eYour inventory was &cfull&e."));
                  Clickable clickable = new Clickable("&a[Click here] &eto recover the items.",
                      "&a[Click here]",
                      "/reclaimitems");
                  clickable.sendToPlayer(trader);
                  trader.sendMessage("");
                }

                trader.sendMessage(CC.translate("&aYou have succesfully traded."));
                target.sendMessage(CC.translate("&aYou have succesfully traded."));

                setClosedByMenu(true);

                target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
                trader.playSound(trader.getLocation(), Sound.LEVEL_UP, 1, 1);

                target.closeInventory();
                trader.closeInventory();
                cancel();
                return;
              }

              target.playSound(target.getLocation(), Sound.NOTE_PLING, 1, 1);
              trader.playSound(trader.getLocation(), Sound.NOTE_PLING, 1, 1);

              countdown -= 1;
            }
          };

          runnable.runTaskTimer(Main.getInstance(), 0, 20);
        }
      }
    });

    HCFProfile targetProfile = HCFProfile.get(target);

    buttons.put(5, Button.fromItem(
            new ItemBuilder(Material.SKULL_ITEM).data((short) 3).setSkullOwner(target.getName()).name("&a&l" + target.getName())
                    .addToLore("",
                            CC.translate("&eBalance: &a" + targetProfile.getEconomyData().getFormattedBalance())).build()));

    buttons.put(0, new Button() {

      @Override
      public String getName(Player player) {
        return "&6&lClose";
      }

      @Override
      public List<String> getDescription(Player player) {
        return Lists.newArrayList("");
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.BED;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
      }
    });

    int maxLeftX = 3;

    int x = 0;
    int y = 1;
    for (List<ItemStack> items : itemsLeft.values()) {
      for (ItemStack item : items) {
        if (item != null) {
          buttons.put(getSlot(x++, y), new ItemButton(item));

          if (x > maxLeftX) {
            x = 0;
            y++;
          }
        }
      }
    }

    int maxRightX = 8;
    x = 5;
    y = 1;

    for (List<ItemStack> items : itemsRight.values()) {
      for (ItemStack item : items) {
        if (item != null) {
          buttons.put(getSlot(x++, y), new ItemButton(item));

          if (x > maxRightX) {
            x = 5;
            y++;
          }
        }
      }
    }

    return buttons;
  }

  @RequiredArgsConstructor
  public class ItemButton extends Button {

    private final ItemStack item;

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
      return item.getType();
    }

    @Override
    public ItemStack getButtonItem(Player player) {
      return item;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {

      List<ItemStack> items = getItemsByPlayer(player);

      if (items.contains(item)) {
        items.remove(item);

        player.getInventory().addItem(item);
      }

      confirmed.clear();

      setClosedByMenu(true);
    }
  }
}