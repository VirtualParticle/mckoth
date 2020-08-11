package game;

import arena.capturePoint.ActiveCapturePoint;
import arena.Map;
import com.virtualparticle.mc.mckoth.McKoth;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Map map;
    private final int id;
    private final List<Team> teams;
    private final List<ActiveCapturePoint> activeCapturePoints;
    private final McKoth plugin;
    private final int targetScore;

    public Game(Map map, int id, int targetScore) {
        this.map = map;
        this.id = id;
        this.targetScore = targetScore;
        this.teams = new ArrayList<>();
        this.plugin = McKoth.getPlugin();
        plugin.getGames().add(this);
        activeCapturePoints = new ArrayList<>();
    }

    public Map getMap() {
        return map;
    }

    public int getId() {
        return id;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<ActiveCapturePoint> getActiveCapturePoints() {
        return activeCapturePoints;
    }

    public void start() {

        int task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

        }, 0, 20);

    }

    public void endRound(Team winningTeam) {
        for (Team team : teams) {
            team.getTimer().reset();
        }
        if (winningTeam.incrementPoints() >= targetScore) {
            endGame(winningTeam);
        }
    }

    private void endGame(Team winningTeam) {

    }

}
