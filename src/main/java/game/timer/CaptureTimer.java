package game.timer;

import game.Game;
import map.capturePoint.ActiveCapturePoint;
import map.events.PointCaptureEvent;
import com.virtualparticle.mc.mckoth.McKoth;
import game.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import utils.ChatUtils;
import utils.MathUtils;

public class CaptureTimer extends Timer {

    private final ActiveCapturePoint capturePoint;
    private final Game game;
    private Team controllingTeam; // team which currently controls the point to which this timer is bound
    private Team teamWithCaptime; // team that currently has capture time (not the team currently on the point)
    private Team capturingTeam; // team on point

    public CaptureTimer(float time, ActiveCapturePoint capturePoint, Game game) {
        super(time);
        this.capturePoint = capturePoint;
        this.game = game;
    }

    public CaptureTimer(float time, float interval, ActiveCapturePoint capturePoint, Game game) {
        super(time, interval);
        this.capturePoint = capturePoint;
        this.game = game;
    }

    @Override
    public void reset() {
        super.reset();
        teamWithCaptime = null;
        capturingTeam = null;
        System.out.println("reset");
    }

    @Override
    public void run() {
        super.run();

        Objective objective = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("score");
        objective.getScore("Capture Point " + capturePoint.getName()).setScore((int) time);

//        System.out.println(time + ", " + (time - interval) + ", " + paused);

        if (time <= 0) {

            if (capturingTeam == teamWithCaptime) {
                // point captured
                System.out.println("point captured by " + capturingTeam);
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

        } else if (time > originalTime && capturingTeam != null) {
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

    public String getProgressBar() {
        ChatColor leftColor = teamWithCaptime != null ? teamWithCaptime.getColor() : ChatColor.GRAY;
        ChatColor rightColor = controllingTeam != null ? controllingTeam.getCapColor() : ChatColor.GRAY;
        return ChatUtils.generateProgressBar(leftColor, rightColor, 125 / 10, time / originalTime);
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
        float speed = (float) MathUtils.harmonicApproximation(count);
        if (capturingTeam == null) {
            interval = -1;
        } else if (capturingTeam != teamWithCaptime && teamWithCaptime != null) {
            interval = -2 * speed; // captime decreases faster when another team is standing on point
        } else {
            interval = speed;
            teamWithCaptime = capturingTeam;
        }
        this.capturingTeam = capturingTeam;
        System.out.println(capturingTeam + " now capturing the point. Speed=" + speed + " Interval=" + interval);
    }

}
