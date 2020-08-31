package game.timer;

import game.Game;
import game.GamePlayer;
import map.capturePoint.ActiveCapturePoint;
import map.events.PointCaptureEvent;
import com.virtualparticle.mc.mckoth.McKoth;
import game.Team;
import org.bukkit.ChatColor;
import utils.ChatUtils;
import utils.MathUtils;

import java.util.List;

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
        updateSpeed();
    }

    public void clear() {
        controllingTeam = null;
        teamWithCaptime = null;
        capturingTeam = null;
    }

    @Override
    public void run() {

        super.run();

        if (teamWithCaptime == null && capturingTeam != null && interval != 0) {
            teamWithCaptime = capturingTeam;
        }

        if (time <= 0) {

            if (capturingTeam == teamWithCaptime) {
                // point captured
                McKoth.getPlugin().getServer().getPluginManager().callEvent(
                        new PointCaptureEvent(controllingTeam, capturingTeam, capturePoint));
                if (controllingTeam != null) {
                    controllingTeam.getTimer().setPaused(true);
                }
                controllingTeam = capturingTeam;
                controllingTeam.getTimer().setPaused(false);
                teamWithCaptime = null;
                capturingTeam = null;
            }
            reset();

        } else if (time > originalTime) {
            // point capture time lost
//            McKoth.getPlugin().getServer().getPluginManager().callEvent(new CaptimeLostEvent(this));
            teamWithCaptime = null;
            reset();
        }

    }

    public void updateSpeed() {

        List<GamePlayer> players = capturePoint.getPlayers();
        int capturingPlayers = (int) players.stream().filter(player -> player.getTeam() == capturingTeam).count();
        int opposingPlayers = players.size() - capturingPlayers;
        double speed = (MathUtils.harmonicApproximation(capturingPlayers) * (opposingPlayers == 0 ? 1 : 0));

        if (players.size() == 0) {
            speed = teamWithCaptime == null && capturingTeam == null ? 0 : -0.5; // natural captime decay
        } else if (capturingTeam != teamWithCaptime && teamWithCaptime != null) {
            speed *= -1; // removing other team's captime
        } else if (capturingTeam == controllingTeam) {
            speed = 0;
        }

        interval = (float) speed;

    }

    public String getProgressBar() {

        ChatColor defaultColor = paused ? ChatColor.DARK_GRAY : ChatColor.GRAY;

        ChatColor leftColor = teamWithCaptime != null ? teamWithCaptime.getColor() : defaultColor;
        ChatColor rightColor = controllingTeam != null ? controllingTeam.getCapColor() : defaultColor;
        int index = game.getTeams().indexOf(teamWithCaptime);
        return ChatUtils.generateProgressBar(leftColor, rightColor, 125 / 10, index % 2 != 0, time / originalTime);
    }

    public Team getControllingTeam() {
        return controllingTeam;
    }

    public void setControllingTeam(Team team) {
        this.controllingTeam = team;
    }

    public Team getCapturingTeam() {
        return capturingTeam;
    }

    public void setCapturingTeam(Team team) {
        this.capturingTeam = team;
    }

    public void setTeamWithCaptime(Team team) {
        this.teamWithCaptime = team;
    }

    public Team getTeamWithCaptime() {
        return teamWithCaptime;
    }

    public Game getGame() {
        return game;
    }

}
