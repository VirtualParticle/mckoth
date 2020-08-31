package game.listeners;

import game.timer.CaptureTimer;
import map.capturePoint.ActiveCapturePoint;
import map.events.PointCaptureEvent;
import game.Game;
import game.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CapturePointListener implements Listener {

    private final Game game;

    public CapturePointListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onPointCapture(PointCaptureEvent e) {

        if (!game.isActive()) {
            return;
        }

        Team currentTeam = e.getCurrentTeam();
        Team capturingTeam = e.getCapturingTeam();
        ActiveCapturePoint capturePoint = e.getCapturePoint();
        if (capturingTeam.getTimer().getTime() <= 0) {
            // this is when a team recaptures after losing the point in overtime
//            game.endRound(capturingTeam);
        }

    }

}
