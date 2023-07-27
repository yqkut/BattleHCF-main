package cc.stormworth.hcf.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class NameMCApi {

  private final String LINK = "https://api.namemc.com/server/battle.rip/likes?profile=";

  public boolean isLiked(Player player) {
    try {
      URL url = new URL(LINK + player.getUniqueId());
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));

      String line = bufferedReader.readLine();
      bufferedReader.close();
      return line.contains("true");
    } catch (Exception ignored) {
    }
    return false;
  }

}