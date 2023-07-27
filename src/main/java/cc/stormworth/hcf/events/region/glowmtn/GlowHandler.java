package cc.stormworth.hcf.events.region.glowmtn;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.region.glowmtn.listeners.GlowListener;
import cc.stormworth.hcf.team.claims.Claim;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class GlowHandler {

    @Getter
    private final static String glowTeamName = "Glowstone";
    @Getter
    public static GlowRespawnTask glowRespawnTask;
    private static File file;
    @Getter
    @Setter
    private GlowMountain glowMountain;

    public GlowHandler() {
        try {
            file = new File(Main.getInstance().getDataFolder(), "glowmtn.json");
            if (!file.exists()) {
                glowMountain = null;
                if (file.createNewFile()) {
                    Main.getInstance().getLogger().warning("Created a new glow mountain json file.");
                }
            } else {
                glowMountain = CorePlugin.GSON.fromJson(FileUtils.readFileToString(file), GlowMountain.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        glowRespawnTask = new GlowRespawnTask(this);

        Main.getInstance().getServer().getPluginManager().registerEvents(new GlowListener(), Main.getInstance());
    }

    public static Claim getClaim() {
        if (Main.getInstance().getTeamHandler().getTeam(glowTeamName).getClaims().isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(CC.RED + "Glowstone Mountain is not set!");
            return null;
        }
        return Main.getInstance().getTeamHandler().getTeam(glowTeamName).getClaims().get(0); // null if no glowmtn is set!
    }

    public void save() {
        try {
            FileUtils.write(file, CorePlugin.GSON.toJson(glowMountain));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGlowMountain() {
        return glowMountain != null;
    }
}