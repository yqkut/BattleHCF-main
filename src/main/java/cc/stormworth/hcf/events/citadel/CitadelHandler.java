package cc.stormworth.hcf.events.citadel;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.citadel.listeners.CitadelListener;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CitadelHandler {

    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";

    private final File CitadelInfo;
    @Getter
    private final Set<ObjectId> cappers = new HashSet<>();

    public CitadelHandler() {
        CitadelInfo = new File(Main.getInstance().getDataFolder(), "citadelInfo.json");

        loadInfo();
        Main.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), Main.getInstance());

        TaskUtil.runAsyncTimer(Main.getInstance(), () -> Main.getInstance().getCitadelHandler().saveInfo(), 0L, 20 * 60 * 5);

        /*new BukkitRunnable() {
            @Override
            public void run() {
                if (CustomTimerCreateCommand.isSOTWTimer()) return;
                if (Main.getInstance().getEventHandler().getEvent("Citadel") == null || !Main.getInstance().getEventHandler().getEvent("Citadel").isActive()) return;
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!DTRBitmask.CITADEL.appliesAt(online.getLocation()) && !DTRBitmask.CONQUEST.appliesAt(online.getLocation())) continue;
                    if (online.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;
                    if (online.hasMetadata("invisible")) {
                        if (online.hasPotionEffect(PotionEffectType.INVISIBILITY)) online.removePotionEffect(PotionEffectType.INVISIBILITY);
                        return;
                    }
                    online.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                    CorePlugin.getInstance().getNametagEngine().reloadPlayer(online);
                    CorePlugin.getInstance().getNametagEngine().reloadOthersFor(online);
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20L, 20L);*/
    }

    public void loadInfo() {
        try {
            if (!CitadelInfo.exists()) {
                CitadelInfo.createNewFile();

                BasicDBObject dbObject = getDefaults();

                FileUtils.write(CitadelInfo, CorePlugin.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            } else {
                BasicDBObject file = (BasicDBObject) JSON.parse(FileUtils.readFileToString(CitadelInfo));

                BasicDBObject defaults = getDefaults();

                defaults.keySet().stream().filter(key -> !file.containsKey(key)).forEach(key -> file.put(key, defaults.get(key)));

                FileUtils.write(CitadelInfo, CorePlugin.GSON.toJson(new JsonParser().parse(file.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(CitadelInfo));

            if (dbo != null) {
                if (dbo.containsKey("cappers")) {
                    BasicDBList cappers = (BasicDBList) dbo.get("cappers");

                    for (Object cappersObj : cappers) {
                        BasicDBObject capper = (BasicDBObject) cappersObj;
                        this.cappers.add(capper.getObjectId("cappers"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveInfo() {
        try {
            BasicDBObject dbo = new BasicDBObject();

            BasicDBList cappers = new BasicDBList();
            BasicDBList chests = new BasicDBList();
            BasicDBList loot = new BasicDBList();

            for (ObjectId capper : this.cappers) {
                BasicDBObject capp = new BasicDBObject();
                capp.put("cappers", capper);
                cappers.add(capp);
            }

            dbo.put("cappers", cappers);
            dbo.put("chests", chests);
            dbo.put("loot", loot);

            CitadelInfo.delete();
            FileUtils.write(CitadelInfo, CorePlugin.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BasicDBObject getDefaults() {
        BasicDBObject dbo = new BasicDBObject();

        dbo.put("cappers", new HashSet<>());
        dbo.put("lootable", new Date());
        dbo.put("chests", new BasicDBList());
        dbo.put("loot", new BasicDBList());

        return dbo;
    }

    public void resetCappers() {
        this.cappers.clear();
    }

    public void addCapper(ObjectId capper) {
        this.cappers.add(capper);
        saveInfo();
    }
}