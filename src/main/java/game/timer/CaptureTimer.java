package game.timer;

import game.Team;
import utils.Utils;

public class CaptureTimer extends Timer {

    private Team controllingTeam; // team which currently controls the point to which this timer is bound
    private Team teamWithCaptime; // team that currently has capture time (not the team currently on the point)
    private Team capturingTeam; // team on point

    public CaptureTimer(long time) {
        super(time);
    }

    public CaptureTimer(long time, long interval) {
        super(time, interval);
    }

    @Override
    public void run() {
        super.run();

        if (time <= 0) {

            if (capturingTeam == teamWithCaptime) {
                // point captured
                controllingTeam = capturingTeam;
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
