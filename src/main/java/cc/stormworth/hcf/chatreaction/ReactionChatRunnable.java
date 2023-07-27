package cc.stormworth.hcf.chatreaction;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.util.misc.InventoryUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReactionChatRunnable implements Runnable, Listener {

  public int seconds = 0;
  public String word = "";
  public boolean enabled = false;
  public final List<String> words = new ArrayList<>();
  public final List<String> commands = new ArrayList<>();
  public List<ItemStack> items = new ArrayList<>();
  private long startedAt;

  public ReactionChatRunnable() {

    words.addAll(Lists.newArrayList(
            "Halloween",
            "Battle",
            "BattleOnTop",
            "6k2",
            "battle.rip/discord",
            "2022",
            "HCF",
            "Kitmap",
            "Map",
            "Season2",
            "Frozeado",
            "Partner",
            "NicoArg17",
            "Applies",
            "Media",
            "store.battle.rip",
            "Jaarabe",
            "Elivann",
            "twitter.com/battleripnet",
            "Houp",
            "Dudas",
            "Staffs",
            "Teamspeak",
            "Discord",
            "Twitch",
            "YouTube",
            "Twitter",
            "Maaxxxii"
    ));


    load();

    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }

  public void save(){
    File file = new File(Main.getInstance().getDataFolder(), "reaction.json");

    try {
      BasicDBObject dbObject = new BasicDBObject();

      dbObject.put("rewards", JSON.parse(CorePlugin.GSON.toJson(items)));

      FileUtils.write(file, CorePlugin.GSON.toJson(new JsonParser().parse(dbObject.toString())));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void load(){
    File file = new File(Main.getInstance().getDataFolder(), "reaction.json");

    if (!file.exists()) {
      return;
    }

    try {
      BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(file));

      if (dbObject != null && dbObject.containsField("rewards")) {
        Type type = new TypeToken<List<ItemStack>>() {}.getType();
        items = CorePlugin.GSON.fromJson(dbObject.getString("rewards"), type);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    if (Main.getInstance().getServerHandler().isEOTW()){
      return;
    }

    /*if (Bukkit.getOnlinePlayers().size() < 19) {
      return;
    }*/

    seconds++;

    if (seconds >= 1200) {
      if (seconds == 1200) {
        enabled = true;

        int random = Main.RANDOM.nextInt(words.size());

        word = words.get(random);

        Bukkit.broadcastMessage(CC.translate("&6&l&m-------------------&f&l&m-----------------&6&l&m-------------"));
        Bukkit.broadcastMessage(CC.translate("&6&lReaction &7» &eType the word &f" + word + " &eto win rewards!"));
        Bukkit.broadcastMessage(CC.translate("&6&l&m-------------------&f&l&m-----------------&6&l&m-------------"));
        startedAt = System.currentTimeMillis();
      }

      if (seconds >= 1500) {
        enabled = false;
        seconds = 0;
        word = "";

        Bukkit.broadcastMessage(CC.translate("&6&l&m-------------------&f&l&m-----------------&6&l&m-------------"));
        Bukkit.broadcastMessage(CC.translate("&6&lReaction Event &ehas been cancelled."));
        Bukkit.broadcastMessage(CC.translate("&6&l&m-------------------&f&l&m-----------------&6&l&m-------------"));
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onReactionChat(AsyncPlayerChatEvent event) {
    if (word.equals("")) {
      return;
    }

    if (!enabled) {
      return;
    }

    if (event.getMessage().equals(word)) {
      event.setCancelled(true);

      enabled = false;
      word = "";
      seconds = 0;

      long timeElapsed = System.currentTimeMillis() - startedAt;

      ItemStack randomReward = items.get(Main.RANDOM.nextInt(items.size()));

      Bukkit.broadcastMessage(CC.translate("&6&l&m-------------------&f&l&m------------------------------"));
      Bukkit.broadcastMessage(CC.translate(""));
      Bukkit.broadcastMessage(CC.translate("&6&lReaction &7» &6" + event.getPlayer().getDisplayName() + " &ewon in &f" + ScoreFunction.TIME_FANCY.apply((float) timeElapsed / 1000) + "&e!"));
      Bukkit.broadcastMessage(CC.translate(""));
      Bukkit.broadcastMessage(CC.translate("&eThe winner has received &7&ox" + randomReward.getAmount() + " " + InventoryUtil.getItemName(randomReward) + "&e!"));
      Bukkit.broadcastMessage(CC.translate(""));
      Bukkit.broadcastMessage(CC.translate("&6&l&m-------------------&f&l&m------------------------------"));

      event.getPlayer().getInventory().addItem(randomReward);
      event.getPlayer().updateInventory();
    }
  }

  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    Inventory inventory = event.getInventory();

    if(inventory.getName().equalsIgnoreCase("Edit Rewards of ChatReaction")){
      items.forEach(inventory::addItem);

      items.clear();
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event){
    Inventory inventory = event.getInventory();

    if(inventory.getName().equalsIgnoreCase("Edit Rewards of ChatReaction")){
        for(int i = 0; i < inventory.getSize(); i++){
            ItemStack item = inventory.getItem(i);

            if(item != null){
              items.add(item);
            }
        }

      save();

        Player player = (Player) event.getPlayer();

        player.sendMessage(CC.translate("&aRewards have been saved."));
    }
  }
}