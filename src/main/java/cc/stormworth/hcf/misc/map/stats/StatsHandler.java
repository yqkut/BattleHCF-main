package cc.stormworth.hcf.misc.map.stats;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.param.ParameterType;
import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.core.util.gson.serialization.LocationSerializer;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.stats.command.StatsTopCommand;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Iterables;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class StatsHandler implements Listener {

  private Map<UUID, StatsEntry> stats = Maps.newConcurrentMap();
  @Getter
  private Map<Location, StatsTopCommand.StatsObjective> objectives = Maps.newHashMap();
  @Getter
  private final Map<Integer, UUID> topKills = Maps.newConcurrentMap();

  public StatsHandler() {
    CorePlugin.getInstance().runRedisCommand(redis -> {
      for (final String key : redis.keys(Main.DATABASE_NAME + ":stats:*")) {
        final UUID uuid = UUID.fromString(key.split(":")[2]);
        final StatsEntry entry = CorePlugin.PLAIN_GSON.fromJson(redis.get(key), StatsEntry.class);
        this.stats.put(uuid, entry);
      }

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] Loaded " + this.stats.size() + " stats.");

      if (redis.exists(Main.DATABASE_NAME + ":objectives")) {
        List<String> serializedObjectives = CorePlugin.PLAIN_GSON.fromJson(
                redis.get(Main.DATABASE_NAME + ":objectives"), new TypeToken<List<String>>() {}.getType());
        for (String objective : serializedObjectives) {

          Location location = LocationSerializer.deserialize((BasicDBObject) JSON.parse(objective.split("----")[0]));
          StatsTopCommand.StatsObjective obj = StatsTopCommand.StatsObjective.valueOf(objective.split("----")[1]);

          this.objectives.put(location, obj);
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[HCF] Loaded " + this.objectives.size() + " objectives.");
      }
      return null;
    });

    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());

    rCommandHandler.registerParameterType(StatsTopCommand.StatsObjective.class, (ParameterType<StatsTopCommand.StatsObjective>) (sender, source) -> {

          for (StatsTopCommand.StatsObjective objective : StatsTopCommand.StatsObjective.values()) {
            if (source.equalsIgnoreCase(objective.getName())) {
              return objective;
            }
            for (String alias : objective.getAliases()) {
              if (source.equalsIgnoreCase(alias)) {
                return objective;
              }
            }
          }
          sender.sendMessage(ChatColor.RED + "Objective '" + source + "' not found.");
          return null;
        });

    Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), this::save, 600L, 600L);
  }

  public void save() {
    CorePlugin.getInstance().runRedisCommand(redis -> {
      List<String> serializedObjectives = this.objectives.entrySet()
              .stream()
              .map(entry -> LocationSerializer.serialize(entry.getKey()) + "----" + (entry.getValue()).name())
              .collect(Collectors.toList());

      redis.set(Main.DATABASE_NAME + ":objectives", CorePlugin.PLAIN_GSON.toJson(serializedObjectives));

      for (StatsEntry entry2 : this.stats.values()) {
        redis.set(Main.DATABASE_NAME + ":stats:" + entry2.getOwner().toString(), CorePlugin.PLAIN_GSON.toJson(entry2));
      }
      return null;
    });
  }

  public StatsEntry getStats(final Player player) {
    return this.getStats(player.getUniqueId());
  }

  public StatsEntry getStats(final String name) {
    return this.getStats(UUIDUtils.uuid(name));
  }

  public StatsEntry getStats(final UUID uuid) {
    this.stats.putIfAbsent(uuid, new StatsEntry(uuid));
    return this.stats.get(uuid);
  }

  private String beautify(Location location) {
    StatsTopCommand.StatsObjective objective = objectives.get(location);
    switch (objective) {
      case DEATHS:
        return "Top Deaths";
      case HIGHEST_KILLSTREAK:
        return "Top KillStrk";
      case KD:
        return "Top KDR";
      case KILLS:
        return "Top Kills";
      default:
        return "Error";
    }
  }

  private String trim(final String name) {
    return (name.length() <= 15) ? name : name.substring(0, 15);
  }

  private StatsEntry get(StatsTopCommand.StatsObjective objective, int place) {
    Map<StatsEntry, Number> base = Maps.newHashMap();

    for (StatsEntry entry : stats.values()) {
      base.put(entry, entry.get(objective));
    }

    TreeMap<StatsEntry, Number> ordered = new TreeMap<>((first, second) -> {
      if (first.get(objective).doubleValue() >= second.get(objective).doubleValue()) {
        return -1;
      }
      return 1;
    });

    ordered.putAll(base);

    Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();

    int index = 0;
    for (Map.Entry<StatsEntry, Number> entry : ordered.entrySet()) {

      if (entry.getKey().getDeaths() < 10 && objective == StatsTopCommand.StatsObjective.KD) {
        continue;
      }

      leaderboards.put(entry.getKey(), entry.getValue() + "");

      index++;

      if (index == place + 1) {
        break;
      }
    }

    try {
      return Iterables.get(leaderboards.keySet(), place - 1);
    } catch (Exception e) {
      return null;
    }
  }

  public void clearAll() {
    this.stats.clear();
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), this::save);
  }

  public void clearLeaderboards() {
    this.objectives.clear();
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), this::save);
  }

  public Map<StatsEntry, String> getLeaderboards(final StatsTopCommand.StatsObjective objective, int range) {
    if (objective != StatsTopCommand.StatsObjective.KD) {
      Map<StatsEntry, Number> base = Maps.newHashMap();

      for (final StatsEntry entry : this.stats.values()) {
        base.put(entry, entry.get(objective));
      }

      TreeMap<StatsEntry, Number> ordered = new TreeMap<>(
          (first, second) -> {
            if (first.get(objective).doubleValue() >= second.get(objective).doubleValue()) {
              return -1;
            } else {
              return 1;
            }
          });

      ordered.putAll(base);
      Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();

      int index = 0;
      for (final Map.Entry<StatsEntry, Number> entry2 : ordered.entrySet()) {
        leaderboards.put(entry2.getKey(), entry2.getValue() + "");
        if (++index == range) {
          break;
        }
      }
      return leaderboards;
    }

    Map<StatsEntry, Double> base2 = Maps.newHashMap();

    for (final StatsEntry entry : this.stats.values()) {
      base2.put(entry, entry.getKD());
    }
    final TreeMap<StatsEntry, Double> ordered2 = new TreeMap<>((first, second) -> {
      if (first.getKD() > second.getKD()) {
        return -1;
      } else {
        return 1;
      }
    });

    ordered2.putAll(base2);

    Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();
    int index = 0;

    for (final Map.Entry<StatsEntry, Double> entry3 : ordered2.entrySet()) {
      if (entry3.getKey().getKD() <= 0.0) {
        continue;
      }
      double kd = entry3.getKey().getKD();
      leaderboards.put(entry3.getKey(), kd + "");
      if (++index == range) {
        break;
      }
    }

    return leaderboards;
  }
}