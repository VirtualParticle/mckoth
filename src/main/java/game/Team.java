package game;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.virtualparticle.mc.mckoth.McKoth;
import game.timer.Timer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    private final McKoth plugin;
    private final String name;
    private final List<GamePlayer> players;
    private final Timer timer;
    private final Material identifier;
    private final Region spawnRegion;
    private int timerTask;
    private int points = 0;
    private float respawnLength = 10;

    public Team(String name, long captime, Region spawnRegion, Material identifier) {
        this.name = name;
        this.timer = new Timer(captime);
        this.spawnRegion = spawnRegion;
        this.identifier = identifier;
        players = new ArrayList<>();
        plugin = McKoth.getPlugin();
    }

    public Team(String name, long captime, Region spawnRegion) {
        this(name, captime, spawnRegion, Material.AIR);
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void enableTimer() {
        timerTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, timer, 0, 20);
    }

    public void disableTimer() {
        plugin.getServer().getScheduler().cancelTask(timerTask);
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

    public Location createRespawnLocation() {

        World world = BukkitAdapter.adapt(Objects.requireNonNull(spawnRegion.getWorld()));
        double x = spawnRegion.getMinimumPoint().getX() + spawnRegion.getWidth() * Math.random();
        double y = spawnRegion.getMinimumPoint().getY();
        double z = spawnRegion.getMinimumPoint().getZ() + spawnRegion.getLength() * Math.random();

        return new Location(world, x, y, z);

    }

    public float getRespawnLength() {
        return respawnLength;
    }

    public void setRespawnLength(float respawnLength) {
        this.respawnLength = respawnLength;
    }

}
