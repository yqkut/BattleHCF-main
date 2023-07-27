package cc.stormworth.hcf.misc.gkits;

public enum KitType {

  PREMIUM,
  FREE;


  public static KitType getByType(String type) {
    for (KitType kitType : KitType.values()) {
      if (kitType.name().equalsIgnoreCase(type)) {
        return kitType;
      }
    }

    return null;
  }

}