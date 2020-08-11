package game.timer;

import arena.capturePoint.ActiveCapturePoint;
import arena.events.PointCaptureEvent;
import com.virtualparticle.mc.mckoth.McKoth;
import game.Team;
import utils.Utils;

public class CaptureTimer extends Timer {

    private final ActiveCapturePoint capturePoint;
    private Team controllingTeam; // team which currently controls the point to which this timer is bound
    private Team teamWithCaptime; // team that currently has capture time (not the team currently on the point)
    private Team capturingTeam; // team on point

    public CaptureTimer(long time, ActiveCapturePoint capturePoint) {
        super(time);
        this.capturePoint = capturePoint;
    }

    public CaptureTimer(long time, long interval, ActiveCapturePoint capturePoint) {
        super(time, interval);
        this.capturePoint = capturePoint;
    }

    @Override
    public void reset() {
        super.reset();
        controllingTeam = null;
        teamWithCaptime = null;
        capturingTeam = null;
    }

    @Override
    public void run() {
        super.run();

        if (time <= 0) {

            if (capturingTeam == teamWithCaptime) {
                // point captured
                McKoth.getPlugin().getServer().getPluginManager().callEvent(
                        new PointCaptureEvent(controllingTeam, capturingTeam, capturePoint)
                );
                if (controllingTeam != null) {
                    controllingTeam.getTimer().setPaused(true);
                }
                controllingTeam = capturingTeam;
                controllingTeam.getTimer().setPaused(false);
                teamWithCaptime = null;
                capturingTeam = null;
                interval = 0;
            }
            reset();

        } else if (time >= originalTime && capturingTeam != null) {
            // point capture time expired
            capturingTeam = null;
            interval = 0;
            reset();
        }

        if (time > originalTime) {
            interval = 0;
            reset();
        }

    }

    public Team getControllingTeam() {
        return controllingTeam;
    }

    public void setControllingTeam(Team controllingTeam) {
        this.controllingTeam = controllingTeam;
    }

    public Team getCapturingTeam() {
        return capturingTeam;
    }

    public void setCapturingTeam(Team capturingTeam, int count) {
        float speed = (float) Utils.harmonicApproximation(count);
        if (capturingTeam == null) {
            interval = -1;
        } else if (capturingTeam != this.teamWithCaptime) {
            interval = -2 * speed; // captime decreases faster when another team is standing on point
        } else {
            interval = speed;
        }
        this.capturingTeam = capturingTeam;
    }

}
