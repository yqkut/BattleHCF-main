package cc.stormworth.hcf.tip;

import cc.stormworth.core.file.ConfigFile;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TipManager {

  private final List<Tip> tips = Lists.newArrayList();
  private final ConfigFile tipsFile;

  public TipManager(Main plugin) {
    tipsFile = new ConfigFile(plugin, "tips.yml");

    ConfigurationSection section = tipsFile.getConfig().getConfigurationSection("TIPS");

    section.getKeys(false).forEach(key -> {

      boolean enable = section.getBoolean(key + ".ENABLED");

      String name_english = section.getString(key + ".ITEM.ENGLISH-NAME");
      List<String> lore_english = section.getStringList(key + ".ITEM.ENGLISH-LORE");

      String name_spanish = section.getString(key + ".ITEM.SPANISH-NAME");
      List<String> lore_spanish = section.getStringList(key + ".ITEM.SPANISH-LORE");

      List<String> message_english = section.getStringList(key + ".ENGLISH-MESSAGE");
      List<String> message_spanish = section.getStringList(key + ".SPANISH-MESSAGE");

      tips.add(
          new Tip(enable,
              key,
              name_english,
              name_spanish,
              lore_english,
              lore_spanish,
              message_english,
              message_spanish)
      );
    });

    Bukkit.getScheduler()
        .runTaskTimerAsynchronously(Main.getInstance(), new TipsRunnable(tips.stream().filter(Tip::isEnable).collect(Collectors.toList())), 20L * 60 * 2,
            20 * 60 * 5);
  }

}