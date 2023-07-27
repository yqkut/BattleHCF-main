package cc.stormworth.hcf.profile.pvptimer;

import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

@Getter
public class PvPTimer {

    @Setter private long endAt;
    private boolean firstTime;
    private boolean paused = false;
    private long pausedAt = 0;
    private long currentTime;

    public PvPTimer(boolean firstTime) {
        this.firstTime = firstTime;

        if (firstTime) {
            endAt = System.currentTimeMillis() + TimeUtil.parseTimeLong("1h");
        } else {
            endAt = System.currentTimeMillis() + TimeUtil.parseTimeLong("30m");
        }

    }

    public PvPTimer(long endAt) {
        this.endAt = endAt;
    }

    public PvPTimer(Document document) {
        this.endAt = document.getLong("endAt") + System.currentTimeMillis();
        this.firstTime = document.getBoolean("firstTime");
    }

    public void start(long time) {
        this.endAt = System.currentTimeMillis() + time;
    }

    public void pause() {

        if (paused) {
            return;
        }

        this.pausedAt = System.currentTimeMillis();
        this.paused = true;
    }

    public void resume() {

        if (!paused) {
            return;
        }

        long time = System.currentTimeMillis() - this.pausedAt;

        this.endAt += time;
        this.currentTime += time;

        this.paused = false;
    }

    public boolean isActive() {
        return getRemaining() > 0;
    }

    public long getRemaining() {

        if (CustomTimerCreateCommand.isSOTWTimer() || Main.getInstance().getServerHandler().isPreEOTW() || Main.getInstance().getMapHandler().isKitMap()) {
            return 0;
        }

        if (paused) {
            return currentTime;
        }

        if (System.currentTimeMillis() == currentTime) {
            return 0;
        }

        if (System.currentTimeMillis() > endAt) {
            return 0;
        }

        return currentTime = endAt - System.currentTimeMillis();
    }

    public Document toDocument() {
        Document document = new Document();
        document.put("endAt", endAt - System.currentTimeMillis());
        document.put("firstTime", firstTime);
        return document;
    }
}
