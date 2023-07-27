package cc.stormworth.hcf.battleplayers.map;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;

@RequiredArgsConstructor
@Getter
@Setter
public class BattlePlayer {

  private final UUID uuid;
  private final String name;

  public BattlePlayer(Document document) {
    this(UUID.fromString(document.getString("uuid")), document.getString("name"));
  }

  public Document serialize() {
    return new Document("uuid", uuid.toString()).append("name", name);
  }

}