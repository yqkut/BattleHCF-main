package cc.stormworth.hcf.brewingstand;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class BrewingStandRunnable implements Runnable{

    private final BrewingStandManager brewingStandManager;

    @Setter @Getter
    private static boolean running = true;

    @Override
    public void run() {

        if (!running) {
            return;
        }

        for (BrewingStand brewingStand : brewingStandManager.getBrewingStands().values()){
            brewingStand.tick();
        }
    }


}
