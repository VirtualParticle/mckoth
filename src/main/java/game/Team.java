package game;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.virtualparticle.mc.mckoth.McKoth;
import game.timer.Timer;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    private final McKoth plugin;
    private final String name;
    private final List<GamePlayer> players;
    private final Timer timer;
    private final Material identifier;
    private final ChatColor color;
    private final ChatColor capColor; // color shown on cap meter when point is captured, usually darker version of color
    private final org.bukkit.scoreboard.Team scoreboardTeam;
    private final List<Region> spawnRegions;
    private int timerTask;
    private int points = 0;
    private float respawnTime = 10;

    public Team(String name, long captime, List<Region> spawnRegions, Material identifier, ChatColor color, ChatColor capColor) {
        this.name = name;
        this.timer = new Timer(captime);
        this.spawnRegions = spawnRegions;
        this.identifier = identifier;
        this.color = color;
        this.capColor = capColor;
        players = new ArrayList<>();
        plugin = McKoth.getPlugin();
        // TODO: maybe use a per-game scoreboard
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(
                this.toString().replaceFirst("^[^@]*@", ""));
        scoreboardTeam.setDisplayName(name);
        scoreboardTeam.setColor(color);
        scoreboardTeam.setAllowFriendlyFire(false);
        scoreboardTeam.setCanSeeFriendlyInvisibles(true);
        scoreboardTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
    }

    public Team(String name, long captime, List<Region> spawnRegions, ChatColor color, ChatColor capColor) {
        this(name, captime, spawnRegions, Material.AIR, color, capColor);
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

    public int getPoints() {
        return points;
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

    public List<Region> getSpawnRegions() {
        return spawnRegions;
    }

    public Location createRespawnLocation() {

        if (spawnRegions.isEmpty()) {
            return null;
        }

        Region spawnRegion = spawnRegions.get((int) (Math.random() * spawnRegions.size()));

        World world = BukkitAdapter.adapt(Objects.requireNonNull(spawnRegion.getWorld()));
        double x = spawnRegion.getMinimumPoint().getX() + spawnRegion.getWidth() * Math.random();
        double y = spawnRegion.getMinimumPoint().getY();
        double z = spawnRegion.getMinimumPoint().getZ() + spawnRegion.getLength() * Math.random();

        Location location = new Location(world, x, y ,z);
        if (!location.getBlock().isPassable()) {
            location.add(0, 1, 0);
        }

        return location;

    }

    public float getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(float respawnTime) {
        this.respawnTime = respawnTime;
    }

    public void addPlayer(GamePlayer player) {
        players.add(player);
        scoreboardTeam.addEntry(player.getPlayer().getName());
    }

    public String getName() {
        return name;
    }

    public GamePlayer remove(Player player) {
        scoreboardTeam.removeEntry(player.getName());
        return players.stream().filter(p -> p.getPlayer() == player).findFirst().orElse(null);
    }

    public boolean hasPlayer(Player player) {
        return players.stream().anyMatch(gp -> gp.getPlayer() == player);
    }

    public ChatColor getColor() {
        return color;
    }

    public ChatColor getCapColor() {
        return capColor;
    }

}
