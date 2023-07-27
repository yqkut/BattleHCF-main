package cc.stormworth.hcf.events.region.oremountain;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.region.oremountain.listeners.OreListener;
import cc.stormworth.hcf.team.claims.Claim;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class OreMountainHandler {

    @Getter
    private final static String oreTeamName = "OreMountain";
    @Getter
    public static OreMountainRespawnTask oreRespawnTask;
    private static File file;
    @Getter
    @Setter
    private OreMountain oreMountain;

    public OreMountainHandler() {
        try {
            file = new File(Main.getInstance().getDataFolder(), "oremtn.json");
            if (!file.exists()) {
                oreMountain = null;
                if (file.createNewFile()) {
                    Main.getInstance().getLogger().warning("Created a new ore mountain json file.");
                }
            } else {
                oreMountain = CorePlugin.GSON.fromJson(FileUtils.readFileToString(file), OreMountain.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        oreRespawnTask = new OreMountainRespawnTask(this);

        Main.getInstance().getServer().getPluginManager().registerEvents(new OreListener(), Main.getInstance());
    }

    public static Claim getClaim() {
        if (Main.getInstance().getTeamHandler().getTeam(oreTeamName).getClaims().isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(CC.RED + "Ore Mountain is not set!");
            return null;
        }
        return Main.getInstance().getTeamHandler().getTeam(oreTeamName).getClaims().get(0); // null if no oremtn is set!
    }

    public void save() {
        try {
            FileUtils.write(file, CorePlugin.GSON.toJson(oreMountain));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasOreMountain() {
        return oreMountain != null;
    }
}