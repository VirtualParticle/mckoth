package game;

import arena.capturePoint.ActiveCapturePoint;
import arena.Map;
import com.sk89q.worldedit.regions.Region;
import com.virtualparticle.mc.mckoth.McKoth;
import game.listeners.CapturePointListener;
import game.listeners.GamePlayerListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
        teams.add(new Team("BLU", CAPTIME, spawnRegions.get(0), Material.BLUE_WOOL));
        teams.add(new Team("RED", CAPTIME, spawnRegions.get(1), Material.RED_WOOL));

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
        teams.forEach(team -> {
            team.enableTimer();
            team.getPlayers().forEach(GamePlayer::respawn);
        });

    }

    public void endRound(Team winningTeam) {
        teams.forEach(team -> team.getTimer().reset());
        if (winningTeam.incrementPoints() >= targetScore) {
            endGame(winningTeam);
        }
    }

    private void endGame(Team winningTeam) {
        active = false; // TODO: this might end up being moved to endRound()
        teams.forEach(Team::disableTimer);
    }

    public boolean isActive() {
        return active;
    }

    public GamePlayer getGamePlayer(Player p) {
        GamePlayer gamePlayer = null;
        for (Team team : teams) {
            for (GamePlayer player : team.getPlayers()) {
                if (player.getPlayer() == p) {
                    gamePlayer = player;
                }
            }
        }
        return gamePlayer;
    }

}
