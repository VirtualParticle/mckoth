package game;

import arena.capturePoint.ActiveCapturePoint;
import arena.Map;
import com.sk89q.worldedit.regions.Region;
import com.virtualparticle.mc.mckoth.McKoth;
import game.listeners.CapturePointListener;
import game.listeners.GamePlayerListener;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static final long CAPTIME = 60 * 3; // three minutes

    private final Map map;
    private final int id;
    private final List<Team> teams;
    private final List<ActiveCapturePoint> activeCapturePoints;
    private final McKoth plugin;
    private final int targetScore;
    private boolean active = false;

    public Game(Map map, int id, int targetScore, List<Region> spawnRegions) {
        this.map = map;
        this.id = id;
        this.targetScore = targetScore;

        teams = new ArrayList<>();
        plugin = McKoth.getPlugin();
        plugin.getGames().add(this);
        activeCapturePoints = new ArrayList<>();

        plugin.getServer().getPluginManager().registerEvents(new CapturePointListener(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GamePlayerListener(this), plugin);

        // TODO: this can be changed to allow more than two teams
        teams.add(new Team("BLU", CAPTIME, Material.BLUE_WOOL, spawnRegions.get(0)));
        teams.add(new Team("RED", CAPTIME, Material.RED_WOOL, spawnRegions.get(1)));

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

        active = true; // TODO: maybe move to follow warmup, maybe not
        teams.forEach(Team::startTimer);

    }

    public void endRound(Team winningTeam) {
        teams.forEach(team -> team.getTimer().reset());
        if (winningTeam.incrementPoints() >= targetScore) {
            endGame(winningTeam);
        }
    }

    private void endGame(Team winningTeam) {
        active = false; // TODO: this might end up being moved to endRound()
    }

    public boolean isActive() {
        return active;
    }

}
