package cc.stormworth.hcf.misc.lunarclient.waypoint;

import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
public class LunarClientWaypoint {

    private String name;
    private int color;

    public LCWaypoint createWaypoint(Location location, String replace) {
        int finalColor = color;

        switch (replace){
            case "conquest-red":
            case "Nether":
            case "Hell":
            case "EOTW":
                finalColor = 16711680;
                break;
            case "conquest-yellow":
                finalColor = 16773169;
                break;
            case "conquest-blue":
                finalColor = 9215;
                break;
            case "conquest-green":
                finalColor = 65024;
                break;
            case "conquest-mid":
                finalColor = 12846031;
                break;
            case "Citadel":
            case "End":
                finalColor = 11731199;
                break;
            case "DTC":
                finalColor = 5353728;
                break;
        }

        return new LCWaypoint(this.name.replace("%name%", replace.replace("conquest-", "")), location, finalColor, true);
    }

    public LCWaypoint createWaypoint(Location location) {
        return new LCWaypoint(this.name, location, color, true);
    }
}