package cc.stormworth.hcf.profile.enderchest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;

@Getter
@Setter
@NoArgsConstructor
public class EnderchestUpgrades {

    private int rows = 1;
    private boolean canUse = false;
    private boolean canUseInCombat = false;

    public EnderchestUpgrades(Document document) {
        this.rows = document.getInteger("rows");
        this.canUse = document.getBoolean("canUse");
        this.canUseInCombat = document.getBoolean("canUseInCombat");
    }

    public Document serialize(){
        return new Document().append("rows", rows)
                .append("canUse", canUse)
                .append("canUseInCombat", canUseInCombat);
    }

}
