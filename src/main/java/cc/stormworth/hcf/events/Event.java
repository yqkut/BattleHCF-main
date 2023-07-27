package cc.stormworth.hcf.events;

public interface Event {
    String getName();

    boolean isActive();

    void setActive(boolean active);

    void tick();

    boolean isHidden();

    void setHidden(boolean hidden);

    boolean activate();

    boolean deactivate();

    EventType getType();
}