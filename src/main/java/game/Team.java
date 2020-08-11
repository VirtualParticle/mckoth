package game;

import game.timer.Timer;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final String name;
    private final List<GamePlayer> players;
    private final Timer timer;
    private final Material identifier;
    private int points = 0;

    public Team(String name, long captime, Material identifier) {
        this.name = name;
        this.timer = new Timer(captime);
        players = new ArrayList<>();
        this.identifier = identifier;
    }

    public Team(String name, long captime) {
        this(name, captime, Material.AIR);
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public Timer getTimer() {
        return timer;
    }

    public int incrementPoints() {
        return incrementPoints(1);
    }

    public int incrementPoints(int val) {
        return points += val;
    }


    public Material getIdentifier() {
        return identifier;
    }

}
