package game;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.virtualparticle.mc.mckoth.McKoth;
import game.timer.TeamTimer;
import game.timer.Timer;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    private final McKoth plugin;
    private final String name;
    private final Game game;
    private final List<GamePlayer> players;
    private final TeamTimer timer;
    private final TeamColor color;
    private final org.bukkit.scoreboard.Team scoreboardTeam;
    private final List<Region> spawnRegions;
    private int timerTask;
    private int points = 0;
    private float respawnTime = 10;

    public Team(String name, long captime, List<Region> spawnRegions, Game game, TeamColor teamColor) {
        this.name = name;
        this.spawnRegions = spawnRegions;
        this.game = game;
        this.color = teamColor;
        players = new ArrayList<>();
        plugin = McKoth.getPlugin();
        // TODO: maybe use a per-game scoreboard
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(
                this.toString().replaceFirst("^[^@]*@", ""));
        scoreboardTeam.setDisplayName(name);
        scoreboardTeam.setColor(color.getColor1());
        scoreboardTeam.setAllowFriendlyFire(false);
        scoreboardTeam.setCanSeeFriendlyInvisibles(true);
        scoreboardTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        this.timer = new TeamTimer(this, captime);
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

    public TeamColor getColor() {
        return color;
    }

    public Game getGame() {
        return game;
    }
}
