package game;

import I18n.I18n;
import com.virtualparticle.mc.mckoth.McKoth;
import game.timer.CountdownTimer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
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
    private boolean dead;

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

    public void equip() {

        Color armorColor = team.getColor().getArmorColor();
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(armorColor);
        helmet.setItemMeta(meta);
        chestplate.setItemMeta(meta);
        leggings.setItemMeta(meta);
        boots.setItemMeta(meta);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        ItemStack fireworks = new ItemStack(Material.FIREWORK_ROCKET, 64);

        ItemMeta crossbowItemMeta = crossbow.getItemMeta();
        crossbowItemMeta.addEnchant(Enchantment.QUICK_CHARGE, 4, true);
        crossbowItemMeta.setUnbreakable(true);
        crossbow.setItemMeta(crossbowItemMeta);

        FireworkMeta fireworksMeta = (FireworkMeta) fireworks.getItemMeta();
        fireworksMeta.setPower(5);
        fireworks.setItemMeta(fireworksMeta);

        EntityEquipment equipment = player.getEquipment();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();

        equipment.setHelmet(helmet);
        equipment.setChestplate(chestplate);
        equipment.setLeggings(leggings);
        equipment.setBoots(boots);

        inventory.setItem(0, sword);
        inventory.setItem(1, crossbow);
        inventory.setItem(40, fireworks);


    }

    public void die() {

        // TODO: remove player from regions on die just in case they aren't removed by spectate

        dead = true;
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

                long t = time - 1;
                String message;
                if (t > 1) {
                    message = i18n.getString("respawnTimePlural", String.valueOf(t));
                } else if (t == 1) {
                    message = i18n.getString("respawnTimeSingular", String.valueOf(t));
                } else if (t == 0) {
                    message = i18n.getString("prepareToRespawn");
                } else {
                    message = "";
                    respawn();
                }

                player.sendTitle("", message, 0, 25, 0);

            }, (long) team.getRespawnTime() + 1);

        }, (int) (killCamLength));

    }

    public void respawn() {

        dead = false;
        Bukkit.getScheduler().cancelTask(respawnTask);
        stopSpectating();
        regenHealth();
        player.setGameMode(DEFAULT_GAMEMODE);
        Location respawnLocation = team.createRespawnLocation();
        if (respawnLocation != null) {
            player.teleport(team.createRespawnLocation());
        }

        equip();

    }

    public boolean isDead() {
        return dead;
    }

}
