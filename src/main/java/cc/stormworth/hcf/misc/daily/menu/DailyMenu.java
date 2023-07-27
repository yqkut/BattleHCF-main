package cc.stormworth.hcf.misc.daily.menu;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.daily.DailyManager;
import cc.stormworth.hcf.misc.daily.data.DailyPlayer;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyMenu extends Menu {

  {
    this.setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return CC.translate("Daily Menu");
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    DailyPlayer dailyPlayer = Main.getInstance().getDailyManager().findDailyPlayerByUUID(player.getUniqueId());

    short orangeData = 1;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
            .name(" ")
            .setGlowing(true);

    for (int i = 0; i < 9; i++) {
      buttons.put(i, Button.fromItem(glass.data(orangeData).build()));
    }

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
              Button.fromItem(glass.data(orangeData).build()));
    }

    buttons.put(4, new Button() {
      @Override
      public ItemStack getButtonItem(Player player) {

        List<String> lore = Lists.newArrayList();

        if(dailyPlayer.getStreak() == 0){
          lore.add("&cYou are not on a streak!");
        }else{
          lore.add("&aYou are on a " + dailyPlayer.getStreak() + "-day streak.");
        }

        HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());

        if(profile == null){
          return null;
        }

        long playtimeTime = profile.getTotalPlayTime();

        lore.add(CC.CHAT_SEPARATOR);
        lore.add("&eYou can claim rewards within the server every");
        lore.add("&eday you log in, so you can keep a streak");
        lore.add("&egoing and earn better rewards.");
        lore.add("");
        lore.add(player.getDisplayName() + "&e has play for &6&l" + ScoreFunction.TIME_FANCY.apply(playtimeTime / 1000F) + "&e.");
        lore.add(CC.CHAT_SEPARATOR);

        return new ItemBuilder(Material.SKULL_ITEM).data((short) 3).name("&6&l" + player.getName() +  "'s &eDailystreak")
                .setSkullOwner(player.getName())
                .setLore(lore)
                .build();
      }
    });

    buttons.put(getSlot(1, 1), new NextButton(dailyPlayer, 1));
    buttons.put(getSlot(2, 1), new NextButton(dailyPlayer, 2));
    buttons.put(getSlot(3, 1), new NextButton(dailyPlayer, 3));
    buttons.put(getSlot(4, 1), new NextButton(dailyPlayer, 4));
    buttons.put(getSlot(5, 1), new NextButton(dailyPlayer, 5));
    buttons.put(getSlot(6, 1), new NextButton(dailyPlayer, 6));
    buttons.put(getSlot(6, 1), new NextButton(dailyPlayer, 6));
    buttons.put(getSlot(7, 1), new NextButton(dailyPlayer, 7));

    return buttons;
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 27;
  }

  private static class NextButton extends Button {

    private final DailyManager dailyManager = Main.getInstance().getDailyManager();

    private final DailyPlayer dailyPlayer;
    private final int reward;

    public NextButton(DailyPlayer dailyPlayer, int reward) {
      this.dailyPlayer = dailyPlayer;
      this.reward = reward;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
      ItemBuilder builder;

      if (!dailyManager.unlockNextReward(reward - 1, dailyPlayer.getNextReward())) {
        builder = new ItemBuilder(Material.POWERED_MINECART);
      }else if (dailyPlayer.getNextRewardLevel() > reward) {
        builder = new ItemBuilder(Material.MINECART);
      }else {
        builder = new ItemBuilder(Material.STORAGE_MINECART);
      }

      List<String> lores = Lists.newArrayList();

      builder.name("&6&lDay " + reward + " &7(Reward)");

      if (!dailyManager.unlockNextReward(reward - 1, dailyPlayer.getNextReward())) {
        lores.add("&cYou can not claim this reward yet!");
        lores.add("&7&m" + Strings.repeat("-", 33));
        lores.add("&eEach day you enter you can clam a ");
        lores.add("&ereward and receive better daily rewards.");

        lores.add("&6Today we offer you:");
        lores.addAll(this.getLore(player, reward, false));

        lores.add("&7&m" + Strings.repeat("-", 33));
        lores.add("&eAvailable in: &c" + ScoreFunction.TIME_FANCY.apply((float) dailyManager.getRemaining(reward - 1, dailyPlayer.getNextReward()) / 1000));
      } else if (dailyPlayer.getNextRewardLevel() > reward) {
        lores.add("&cYou already claimed this reward!");
        lores.add("&7&m" + Strings.repeat("-", 33));
        lores.add("&eEach day you enter you can clam a ");
        lores.add("&ereward and receive better daily rewards.");

        lores.add("&6Today we offer you:");
        lores.addAll(this.getLore(player, reward, true));

        lores.add("&7&m" + Strings.repeat("-", 33));
        lores.add("&cWait until next week!");
      } else {
        lores.add("&aYou can claim this reward now!");
        lores.add("&7&m" + Strings.repeat("-", 33));
        lores.add("&eEach day you enter you can clam a ");
        lores.add("&ereward and receive better daily rewards.");

        lores.add("&6Today we offer you:");
        lores.addAll(this.getLore(player, reward, false));

        lores.add("&7&m" + Strings.repeat("-", 33));
        lores.add("&eClick to claim this reward!");
      }

      builder.setLore(lores);

      return builder.build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      if (!dailyManager.unlockNextReward(reward - 1, dailyPlayer.getNextReward())) {
        player.closeInventory();

        player.sendMessage(CC.translate("&cYou don't have access to this reward yet."));
        return;
      }

      if (dailyPlayer.getNextRewardLevel() > reward) {
        player.sendMessage(CC.translate("&cYou already claimed the rewards."));
        return;
      }

      this.getRewards(player, reward);
    }

    public List<String> getLore(Player player, int rewardLevel, boolean claimed) {
      List<String> lore = new ArrayList<>();

      if(Main.getInstance().getMapHandler().isKitMap()){
        switch (rewardLevel) {
          case 1: {
            if (claimed) {
              lore.add("&7&mx10 Evil Keys");
              lore.add("&7&mx5 Horror Keys");
              lore.add("&7&mx2 Halloween Keys");
              lore.add("&7&mx1 Magic Key");
              lore.add("&a&m$1,500 Balance");
            }else{
              lore.add("&e- &7x10 &5&lEvil Keys");
              lore.add("&e- &7x5 &c&lHorror Keys");
              lore.add("&e- &7x2 &dHalloween Keys");
              lore.add("&e- &7x1 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$1,500 Balance");
            }
            break;
          }
          case 2: {
            if (claimed) {
              lore.add("&7&mx15 Evil Keys");
              lore.add("&7&mx10 Horror Keys");
              lore.add("&7&mx9 Halloween Keys");
              lore.add("&7&mx5 Magic Keys");
              lore.add("&a&m$3,000 Balance");
            }else {
              lore.add("&e- &7x15 &5&lEvil Keys");
              lore.add("&e- &7x10 &c&lHorror Keys");
              lore.add("&e- &7x9 &dHalloween Keys");
              lore.add("&e- &7x5 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$3,000 Balance");
            }
            break;
          }
          case 3: {
            if (claimed) {
              lore.add("&7&mx20 Evil Keys");
              lore.add("&7&mx13 Horror Keys");
              lore.add("&7&mx12 Halloween Keys");
              lore.add("&7&mx9 Magic Key");
              lore.add("&a&m$4,500 Balance");
            }else{
              lore.add("&e- &7x20 &5&lEvil Keys");
              lore.add("&e- &7x13 &c&lHorror Keys");
              lore.add("&e- &7x12 &dHalloween Keys");
              lore.add("&e- &7x9 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$4,500 Balance");
            }
            break;
          }
          case 4: {
            if (claimed) {
              lore.add("&7&mx25 Evil Keys");
              lore.add("&7&mx16 Horror Keys");
              lore.add("&7&mx15 Halloween Keys");
              lore.add("&7&mx11 Magic Key");
              lore.add("&a&m$6,000 Balance");
            }else{
              lore.add("&e- &7x25 &5&lEvil Keys");
              lore.add("&e- &7x16 &c&lHorror Keys");
              lore.add("&e- &7x15 &dHalloween Keys");
              lore.add("&e- &7x11 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$6,000 Balance");
            }
            break;
          }
          case 5: {
            if (claimed) {
              lore.add("&7&mx30 Evil Keys");
              lore.add("&7&mx19 Horror Keys");
              lore.add("&7&mx18 Halloween Keys");
              lore.add("&7&mx13 Magic Key");
              lore.add("&a&m$7,500 Balance");
            }else{
              lore.add("&e- &7x30 &5&lEvil Keys");
              lore.add("&e- &7x19 &c&lHorror Keys");
              lore.add("&e- &7x18 &dHalloween Keys");
              lore.add("&e- &7x13 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$7,500 Balance");
            }
            break;
          }
          case 6: {
            if (claimed) {
              lore.add("&7&mx35 Evil Keys");
              lore.add("&7&mx21 Horror Keys");
              lore.add("&7&mx21 Halloween Keys");
              lore.add("&7&mx15 Magic Key");
              lore.add("&a&m$9,000 Balance");
            }else{
              lore.add("&e- &7x35 &5&lEvil Keys");
              lore.add("&e- &7x21 &c&lHorror Keys");
              lore.add("&e- &7x21 &dHalloween Keys");
              lore.add("&e- &7x15 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$9,000 Balance");
            }
            break;
          }
          case 7: {
            if (claimed) {
              lore.add("&7&mx40 Evil Keys");
              lore.add("&7&mx23 Horror Keys");
              lore.add("&7&mx23 Halloween Keys");
              lore.add("&7&mx17 Magic Key");
              lore.add("&a$10,000 Balance");
            }else{
              lore.add("&e- &7x40 &5&lEvil Keys");
              lore.add("&e- &7x23 &c&lHorror Keys");
              lore.add("&e- &7x23 &dHalloween Keys");
              lore.add("&e- &7x17 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$10,000 Balance");
            }
            break;
          }
        }
      }else{
        switch (rewardLevel) {
          case 1: {
            if (claimed) {
              lore.add("&7&mx20 Evil Keys");
              lore.add("&7&mx10 Horror Keys");
              lore.add("&7&mx4 PartnerH Keys");
              lore.add("&7&mx2 Magic Key");
              lore.add("&a&m$1,500 Balance");
            }else{
              lore.add("&e- &7x20 &5&lEvil Keys");
              lore.add("&e- &7x10 &c&lHorror Keys");
              lore.add("&e- &7x4 &b&lP&f&la&b&lr&f&lt&b&ln&f&le&b&lr Keys");
              lore.add("&e- &7x2 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$1,500 Balance");
            }
            break;
          }
          case 2: {
            if (claimed) {
              lore.add("&7&mx30 Evil Keys");
              lore.add("&7&mx20 Horror Keys");
              lore.add("&7&mx18 PartnerH Keys");
              lore.add("&7&mx10 Magic Keys");
              lore.add("&a&m$3,000 Balance");
            }else {
              lore.add("&e- &7x30 &5&lEvil Keys");
              lore.add("&e- &7x20 &c&lHorror Keys");
              lore.add("&e- &7x18 &b&lP&f&la&b&lr&f&lt&b&ln&f&le&b&lr Keys");
              lore.add("&e- &7x10 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$3,000 Balance");
            }
            break;
          }
          case 3: {
            if (claimed) {
              lore.add("&7&mx40 Evil Keys");
              lore.add("&7&mx26 Horror Keys");
              lore.add("&7&mx24 PartnerH Keys");
              lore.add("&7&mx18 Magic Key");
              lore.add("&a&m$4,500 Balance");
            }else{
              lore.add("&e- &7x40 &5&lEvil Keys");
              lore.add("&e- &7x26 &c&lHorror Keys");
              lore.add("&e- &7x24 &b&lP&f&la&b&lr&f&lt&b&ln&f&le&b&lr Keys");
              lore.add("&e- &7x18 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$4,500 Balance");
            }
            break;
          }
          case 4: {
            if (claimed) {
              lore.add("&7&mx50 Evil Keys");
              lore.add("&7&mx32 Horror Keys");
              lore.add("&7&mx30 PartnerH Keys");
              lore.add("&7&mx22 Magic Key");
              lore.add("&a&m$6,000 Balance");
            }else{
              lore.add("&e- &7x50 &5&lEvil Keys");
              lore.add("&e- &7x32 &c&lHorror Keys");
              lore.add("&e- &7x30 &b&lP&f&la&b&lr&f&lt&b&ln&f&le&b&lr Keys");
              lore.add("&e- &7x22 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$6,000 Balance");
            }
            break;
          }
          case 5: {
            if (claimed) {
              lore.add("&7&mx60 Evil Keys");
              lore.add("&7&mx38 Horror Keys");
              lore.add("&7&mx36 PartnerH Keys");
              lore.add("&7&mx26 Magic Key");
              lore.add("&a&m$7,500 Balance");
            }else{
              lore.add("&e- &7x60 &5&lEvil Keys");
              lore.add("&e- &7x38 &c&lHorror Keys");
              lore.add("&e- &7x36 &b&lP&f&la&b&lr&f&lt&b&ln&f&le&b&lr Keys");
              lore.add("&e- &7x26 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$7,500 Balance");
            }
            break;
          }
          case 6: {
            if (claimed) {
              lore.add("&7&mx70 Evil Keys");
              lore.add("&7&mx42 Horror Keys");
              lore.add("&7&mx42 PartnerH Keys");
              lore.add("&7&mx30 Magic Key");
              lore.add("&a&m$9,000 Balance");
            }else{
              lore.add("&e- &7x70 &5&lEvil Keys");
              lore.add("&e- &7x42 &c&lHorror Keys");
              lore.add("&e- &7x42 &b&lP&f&la&b&lr&f&lt&b&ln&f&le&b&lr Keys");
              lore.add("&e- &7x15 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$9,000 Balance");
            }
            break;
          }
          case 7: {
            if (claimed) {
              lore.add("&7&mx80 Evil Keys");
              lore.add("&7&mx46 Horror Keys");
              lore.add("&7&mx46 PartnerH Keys");
              lore.add("&7&mx34 Magic Key");
              lore.add("&a$10,000 Balance");
            }else{
              lore.add("&e- &7x80 &5&lEvil Keys");
              lore.add("&e- &7x46 &c&lHorror Keys");
              lore.add("&e- &7x46 &b&lP&f&la&b&lr&f&lt&b&ln&f&le&b&lr Keys");
              lore.add("&e- &7x34 &e&lM&f&la&e&lg&f&li&e&lc Key");
              lore.add("&e- &a$10,000 Balance");
            }
            break;
          }
        }
      }

      return lore;
    }

    public void getRewards(Player player, int rewardLevel) {
      if(Main.getInstance().getMapHandler().isKitMap()){
        switch (rewardLevel) {
          case 1: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 10");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 5");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Halloween 2");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 1");

            HCFProfile.get(player).getEconomyData().addBalance(1500);

            break;
          }
          case 2: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 15");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 10");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Halloween 9");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 5");

            HCFProfile.get(player).getEconomyData().addBalance(3000);
            break;
          }
          case 3: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 20");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 13");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Halloween 12");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 9");

            HCFProfile.get(player).getEconomyData().addBalance(4500);
            break;
          }
          case 4: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 25");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 16");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Halloween 15");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 11");

            HCFProfile.get(player).getEconomyData().addBalance(6000);
            break;
          }
          case 5: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 30");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 19");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Halloween 18");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 13");

            HCFProfile.get(player).getEconomyData().addBalance(7500);
            break;
          }
          case 6: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 35");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 21");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Halloween 21");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 15");

            HCFProfile.get(player).getEconomyData().addBalance(9000);
            break;
          }
          case 7: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 40");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 23");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Halloween 23");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 17");

            HCFProfile.get(player).getEconomyData().addBalance(10000);
            break;
          }
        }
      }else{
        switch (rewardLevel) {
          case 1: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 20");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 10");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 4");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 2");

            HCFProfile.get(player).getEconomyData().addBalance(1500);
            break;
          }
          case 2: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 30");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 20");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 18");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 10");

            HCFProfile.get(player).getEconomyData().addBalance(3000);
            break;
          }
          case 3: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 40");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 26");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 24");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 18");

            HCFProfile.get(player).getEconomyData().addBalance(4500);
            break;
          }
          case 4: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 50");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 32");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 30");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 22");

            HCFProfile.get(player).getEconomyData().addBalance(6000);
            break;
          }
          case 5: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 60");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 38");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 36");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 26");

            HCFProfile.get(player).getEconomyData().addBalance(7500);
            break;
          }
          case 6: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 70");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 42");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 42");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 30");

            HCFProfile.get(player).getEconomyData().addBalance(9000);
            break;
          }
          case 7: {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Evil 80");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Horror 46");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " PartnerH 46");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givekey " + player.getName() + " Magic 34");

            HCFProfile.get(player).getEconomyData().addBalance(10000);
            break;
          }
        }
      }

      if (dailyPlayer.getNextRewardLevel() < 8) {
        dailyPlayer.setNextRewardLevel(dailyPlayer.getNextRewardLevel() + 1);
      }

      dailyPlayer.setStreak(dailyPlayer.getStreak() + 1);

      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
      player.sendMessage(CC.translate("&aYou successfully received your &eDay #" + rewardLevel + " &areward."));
    }
  }
}
