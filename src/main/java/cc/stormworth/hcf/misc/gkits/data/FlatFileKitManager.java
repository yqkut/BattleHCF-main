package cc.stormworth.hcf.misc.gkits.data;

import cc.stormworth.core.file.ConfigFile;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.gkits.Kit;
import cc.stormworth.hcf.misc.gkits.event.KitRenameEvent;
import cc.stormworth.hcf.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FlatFileKitManager implements Listener {

  private static Map<String, Kit> kitNameMap;
  private final Map<UUID, Kit> kitUUIDMap;
  private ConfigFile config;
  private List<Kit> kits;

  public FlatFileKitManager(final Main plugin) {
    ConfigurationSerialization.registerClass(Kit.class);
    this.kits = new ArrayList<>();
    kitNameMap = new CaseInsensitiveMap<>();
    this.kitUUIDMap = new HashMap<>();
    this.config = new ConfigFile(Main.getInstance(), "kits.yml");
    this.reloadKitData();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public static Kit getKit(final String id) {
    return kitNameMap.get(id);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onKitRename(final KitRenameEvent event) {
    kitNameMap.remove(event.getOldName());
    kitNameMap.put(event.getNewName(), event.getKit());
  }

  public List<Kit> getKits() {
    return this.kits;
  }

  public void createKit(final Kit kit) {
    if (this.kits.add(kit)) {
      kitNameMap.put(kit.getName(), kit);
      this.kitUUIDMap.put(kit.getUniqueID(), kit);
    }
  }

  public void removeKit(final Kit kit) {
    if (this.kits.remove(kit)) {
      kitNameMap.remove(kit.getName());
      this.kitUUIDMap.remove(kit.getUniqueID());
    }
  }

  public void reloadKitData() {
    this.config = new ConfigFile(Main.getInstance(), "kits.yml");
    final Object object = this.config.getConfig().get("kits");
    if (object instanceof List) {
      this.kits = Utils.createList(object, Kit.class);
      for (final Kit kit : this.kits) {
        kitNameMap.put(kit.getName(), kit);
        this.kitUUIDMap.put(kit.getUniqueID(), kit);
      }
    }
  }

  public void saveKitData() {
    this.config.getConfig().set("kits", this.kits);
    this.config.save();
  }
}