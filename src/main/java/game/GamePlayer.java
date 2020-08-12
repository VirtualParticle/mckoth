package game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class GamePlayer {

    private Team team;
    private final Player player;
    private boolean spectating = false;

    public GamePlayer(Player player) {
        this(player, null);
    }

    public GamePlayer(Player player, Team team) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
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

        if (!spectating) {
            player.setGameMode(GameMode.SPECTATOR);
            spectating = true;
        }

        double distance = 5; // how far the camera should be
        float pitch = 30; // 90 is down, -90 is up, 0 is horizon
        float yaw = location.getYaw(); // 0 is positive z, 270 is positive x
        double height = Math.sin(Math.toRadians(pitch)) * distance;
        double length = Math.cos(Math.toRadians(pitch)) * distance;

        double x = location.getX() + Math.sin(Math.toRadians(yaw)) * length;
        double y = location.getY() + height;
        double z = location.getZ() + Math.cos(Math.toRadians(yaw)) * length;

        Location camLocation = new Location(location.getWorld(), x, y, z, yaw, pitch);

        player.teleport(camLocation);

    }

    public boolean isSpectating() {
        return spectating;
    }

}
