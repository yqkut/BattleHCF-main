package cc.stormworth.hcf.events.region.nether;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.region.nether.listeners.NetherListener;
import cc.stormworth.hcf.team.claims.Claim;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class NetherHandler {

    @Getter
    private final static String teamName = "Nether";
    @Getter
    public static NetherRespawnTask task;
    private static File file;
    @Getter
    @Setter
    private NetherArea area;

    public NetherHandler() {
        try {
            file = new File(Main.getInstance().getDataFolder(), "nether.json");
            if (!file.exists()) {
                area = null;
                if (file.createNewFile()) {
                    Main.getInstance().getLogger().warning("Created a new nether json file.");
                }
            } else {
                area = CorePlugin.GSON.fromJson(FileUtils.readFileToString(file), NetherArea.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        task = new NetherRespawnTask(this);

        Main.getInstance().getServer().getPluginManager().registerEvents(new NetherListener(), Main.getInstance());
    }

    public static Claim getClaim() {
        if (Main.getInstance().getTeamHandler().getTeam(teamName) != null && Main.getInstance().getTeamHandler().getTeam(teamName).getClaims().isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(CC.RED + "Nether is not set!");
            return null;
        }
        return Main.getInstance().getTeamHandler().getTeam(teamName).getClaims().get(0);
    }

    public void save() {
        try {
            FileUtils.write(file, CorePlugin.GSON.toJson(area));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasArea() {
        return area != null;
    }
}