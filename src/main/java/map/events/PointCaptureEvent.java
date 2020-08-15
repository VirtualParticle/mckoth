package map.events;

import map.capturePoint.ActiveCapturePoint;
import game.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PointCaptureEvent extends Event {

    public static final HandlerList HANDLERS = new HandlerList();

    private final Team currentTeam;
    private final Team capturingTeam;
    private final ActiveCapturePoint capturePoint;

    public PointCaptureEvent(Team currentTeam, Team capturingTeam, ActiveCapturePoint capturePoint) {
        this.currentTeam = currentTeam;
        this.capturingTeam = capturingTeam;
        this.capturePoint = capturePoint;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public Team getCapturingTeam() {
        return capturingTeam;
    }

    public ActiveCapturePoint getCapturePoint() {
        return capturePoint;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
