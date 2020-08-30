package game.timer.events;

import game.timer.Timer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerExpireEvent extends Event {

    public static final HandlerList HANDLERS = new HandlerList();

    private final Timer timer;

    public TimerExpireEvent(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer() {
        return timer;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
