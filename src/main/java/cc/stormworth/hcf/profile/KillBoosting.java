package cc.stormworth.hcf.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class KillBoosting {

  private final UUID target;
  private long cooldown;
  private int kills;

  public void addKills(int amount) {
    this.kills += amount;
  }

  public KillBoosting(Document document) {
    this.target = UUID.fromString(document.getString("target"));
    this.cooldown = document.getLong("cooldown") + System.currentTimeMillis();
    this.kills = document.getInteger("kills");
  }

  public boolean isOnCooldown() {
    return System.currentTimeMillis() < this.cooldown;
  }

  public boolean isExpired(){
    return System.currentTimeMillis() > this.cooldown;
  }

  public Document serialize(){
    return new Document("target", this.target.toString())
            .append("cooldown", this.cooldown - System.currentTimeMillis())
            .append("kills", this.kills);
  }

}