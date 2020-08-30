package game;

import I18n.I18n;
import com.virtualparticle.mc.mckoth.McKoth;
import game.timer.CountdownTimer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import utils.ChatUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class GamePlayer {

    public static final GameMode DEFAULT_GAMEMODE = GameMode.ADVENTURE;

    private final McKoth plugin;
    private Team team;
    private final Player player;
    private boolean spectating = false;
    private boolean spectatingLocation = false;
    private final I18n i18n = I18n.getInstance();

    private int respawnTask;

    public GamePlayer(Player player) {
        this(player, null);
    }

    public GamePlayer(Player player, Team team) {
        this.player = player;
        this.team = team;
        plugin = McKoth.getPlugin();
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public void regenHealth() {
        player.setHealth(getMaxHealth());
    }

    public int getMaxHealth() {
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        int maxHealth = 20;
        if (maxHealthAttribute != null) {
            maxHealth = (int) maxHealthAttribute.getValue();
        }
        return maxHealth;
    }


    public void spectate(Location location) {

        if (player.getGameMode() != GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SPECTATOR);
        }

        double distance = 5; // how far the camera should be
        float pitch = 60; // 90 is down, -90 is up, 0 is horizon
        float yaw = location.getYaw(); // 0 is positive z, 270 is positive x
        double height = Math.sin(Math.toRadians(pitch)) * distance;
        double length = Math.cos(Math.toRadians(pitch)) * distance;

        double x = location.getX() + Math.sin(Math.toRadians(yaw)) * length;
        double y = location.getY() + height;
        double z = location.getZ() - Math.cos(Math.toRadians(yaw)) * length;

        Location camLocation = new Location(location.getWorld(), x, y, z, yaw, pitch);

        player.teleport(camLocation);

        spectatingLocation = true;
        spectating = true;

    }

    public void spectate(Entity entity) {

        spectatingLocation = false;
        spectating = true;

        if (player.getGameMode() != GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SPECTATOR);
        }

        player.setSpectatorTarget(entity);

    }

    public void stopSpectating() {
        spectatingLocation = false;
        spectating = false;
        player.setGameMode(DEFAULT_GAMEMODE);
    }

    public boolean isSpectatingLocation() {
        return spectatingLocation;
    }

    public boolean isSpectating() {
        return spectating;
    }

    public void die() {

        // TODO: remove player from regions on die just in case they aren't removed by spectate

        regenHealth();
        spectate(player.getLocation());

        float killCamLength = 3 * 20;
        Player killer = player.getKiller();
        if (killer != null) {
            player.sendTitle("", i18n.getString("killedByPlayerMessage",
                    killer.getName()), 0, (int) killCamLength + 5, 0);
        } else {
            player.sendTitle("", i18n.getString("killedByEnvironment"), 0, (int) killCamLength + 5, 0);
        }

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, () -> {

            Player target = team.getPlayers().get((int) (team.getPlayers().size() * Math.random())).getPlayer();
            if (target != player && target != null) {
                spectate(target);
            }

            CountdownTimer.createCountdownTimer(time -> {

                String message;
                if (time > 1) {
                    message = i18n.getString("respawnTimePlural", String.valueOf(time));
                } else if (time == 1) {
                    message = i18n.getString("respawnTimeSingular", String.valueOf(time));
                } else if (time == 0) {
                    message = i18n.getString("prepareToRespawn");
                } else {
                    message = "";
                    respawn();
                }

                player.sendTitle("", message, 0, 25, 0);

            }, (long) team.getRespawnTime());

        }, (int) (killCamLength));

    }

    public void respawn() {

        Bukkit.getScheduler().cancelTask(respawnTask);
        stopSpectating();
        regenHealth();
        player.setGameMode(DEFAULT_GAMEMODE);
        Location respawnLocation = team.createRespawnLocation();
        if (respawnLocation != null) {
            player.teleport(team.createRespawnLocation());
        }

    }

}
