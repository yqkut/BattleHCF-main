package cc.stormworth.hcf.profile.economy;

import cc.stormworth.hcf.util.number.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class EconomyData {

    private double balance;

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void subtractBalance(double amount) {
        this.balance -= amount;
    }

    public String getFormattedBalance() {
        return "$" + NumberUtils.addComma(this.balance);
    }


}
