package game;

import arena.CapturePoint;
import arena.Map;
import com.virtualparticle.mc.mckoth.McKoth;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Map map;
    private final int id;
    private final List<Team> teams;
    private final List<CapturePoint.ActiveCapturePoint> activeCapturePoints;

    public Game(Map map, int id) {
        this.map = map;
        this.id = id;
        this.teams = new ArrayList<>();
        McKoth.getPlugin().getGames().add(this);
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

    public List<CapturePoint.ActiveCapturePoint> getActiveCapturePoints() {
        return activeCapturePoints;
    }

}
