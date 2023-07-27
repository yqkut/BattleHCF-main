package cc.stormworth.hcf.util.number;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtils {

  public String addComma(int num) {
    return String.format("%,d", num);
  }

  public String addComma(double num) {
    return String.format("%,.2f", num);
  }

  public boolean isEven(int num) {
    return num % 2 == 0;
  }

}