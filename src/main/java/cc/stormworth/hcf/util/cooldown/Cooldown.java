package cc.stormworth.hcf.util.cooldown;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class Cooldown {

  private final String name;
  private final long startAt = System.currentTimeMillis();
  private final long endAt;

  @Setter private String expireMessage;

  @Setter private boolean announced;

  public Cooldown(String name, long time, String expireMessage) {
    this.name = name;
    this.endAt = time;
    this.expireMessage = expireMessage;
  }

  public long getTime() {
    return endAt - System.currentTimeMillis();
  }

}