package game;

import map.capturePoint.ActiveCapturePoint;
import map.Map;
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

    protected Game(Map map, int id) {
        this(map, id, map.getTargetScore());
    }

    protected Game(Map map, int id, int targetScore) {
        this.map = map;
        this.id = id;
        this.targetScore = targetScore;

        teams = new ArrayList<>();
        plugin = McKoth.getPlugin();
        activeCapturePoints = new ArrayList<>();

        plugin.getServer().getPluginManager().registerEvents(new CapturePointListener(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GamePlayerListener(this), plugin);

        // TODO: this can be changed to allow more than two teams
        teams.add(new Team("BLU", CAPTIME, map.getSpawnRegions().get(0), Material.BLUE_WOOL));
        teams.add(new Team("RED", CAPTIME, map.getSpawnRegions().get(1), Material.RED_WOOL));

    }

    public GamePlayer addPlayer(Player player) {
        Team smallestTeam = teams.get(0); // assuming teams.size() > 0
        for (Team team : teams) {
            if (team.getPlayers().size() < smallestTeam.getPlayers().size()) {
                smallestTeam = team;
            }
        }
        GamePlayer gamePlayer = new GamePlayer(player, smallestTeam);
        smallestTeam.addPlayer(gamePlayer);
        return gamePlayer;
    }

    public boolean removePlayer(Player player) {

        GamePlayer gamePlayer = null;
        for (Team team : teams) {
            GamePlayer gp = team.remove(player);
            gamePlayer = gp != null ? gp : gamePlayer;
        }

        if (gamePlayer != null) {
            GamePlayer finalGamePlayer = gamePlayer;
            activeCapturePoints.forEach(cp -> cp.removePlayer(finalGamePlayer));
            return true;
        }

        return false;
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

    public boolean hasPlayer(Player player) {
        return teams.stream().anyMatch(team -> team.hasPlayer(player));
    }
}
