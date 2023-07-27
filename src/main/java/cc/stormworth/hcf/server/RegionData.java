package cc.stormworth.hcf.server;

import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegionData {
    private RegionType regionType;
    private Team data;

    public RegionData(final RegionType regionType, final Team data) {
        this.regionType = regionType;
        this.data = data;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof RegionData)) {
            return false;
        }
        final RegionData other = (RegionData) obj;
        return other.regionType == this.regionType && (this.data == null || other.data.equals(this.data));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getName(final Player player, final boolean chat) {
        if (this.data != null) {
            return this.data.getName(player, chat);
        }
        switch (this.regionType) {
            case WARZONE: {
                return ChatColor.RED + "Warzone";
            }
            case WILDNERNESS: {
                return ChatColor.DARK_GREEN + "Wilderness";
            }
            default: {
                return ChatColor.DARK_RED + "N/A";
            }
        }
    }

    public String getName(final Player player) {
        return this.getName(player, false);
    }

    public RegionType getRegionType() {
        return this.regionType;
    }

    public void setRegionType(final RegionType regionType) {
        this.regionType = regionType;
    }

    public Team getData() {
        return this.data;
    }

    public void setData(final Team data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RegionData(regionType=" + this.getRegionType() + ", data=" + this.getData() + ")";
    }
}
