package game;

import com.sk89q.worldedit.regions.Region;
import game.timer.Timer;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final String name;
    private final List<GamePlayer> players;
    private final Timer timer;
    private final Material identifier;
    private final Region spawnRegion;
    private int points = 0;

    public Team(String name, long captime, Material identifier, Region spawnRegion) {
        this.name = name;
        this.timer = new Timer(captime);
        this.spawnRegion = spawnRegion;
        players = new ArrayList<>();
        this.identifier = identifier;
    }

    public Team(String name, long captime, Region spawnRegion) {
        this(name, captime, Material.AIR, spawnRegion);
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

    public Region getSpawnRegion() {
        return spawnRegion;
    }

}
