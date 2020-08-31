package game.listeners;

import I18n.I18n;
import game.Team;
import map.capturePoint.ActiveCapturePoint;
import map.Map;
import game.Game;
import game.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import utils.MathUtils;

public class GamePlayerListener implements Listener {

    private static final I18n i18n = I18n.getInstance();

    private final Game game;

    public GamePlayerListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player p = event.getPlayer();
        GamePlayer gamePlayer = game.getGamePlayer(p);
        if (gamePlayer == null) {
            return;
        }

        if (gamePlayer.isSpectating()) {

            // the reason this is nested is in case there should be special behavior for spectating entities as well
            if (gamePlayer.isSpectatingLocation()) {
                event.setCancelled(true);
            }

            return;
        }

        if (!game.isActive()) {
            return;
        }

        Location locationFrom = event.getFrom();
        Location locationTo = event.getTo();

        if (game.isFrozen()) {
            // let player spin around
            event.setCancelled(locationFrom.distanceSquared(locationTo) != 0);
            return;
        }

        /* TODO: this might cause lag, so consider doing this on a regular interval instead of on event.
            It is also possible to implement a cool-down on this so it only runs it every-so-often */

        Map map = game.getMap();
        if (MathUtils.insideRegion(locationTo, map.getRegion())) {
            for (ActiveCapturePoint point : game.getActiveCapturePoints()) {

                if (MathUtils.insideRegion(locationTo, point.getCapturePoint().getRegion())
                        || MathUtils.insideRegion(locationFrom, point.getCapturePoint().getRegion())) {
                    if (MathUtils.insideRegion(locationTo, point.getCapturePoint().getRegion()) && !point.containsPlayer(gamePlayer)) {
                        point.addPlayer(gamePlayer);
                    } else if (!MathUtils.insideRegion(locationTo, point.getCapturePoint().getRegion()) && point.containsPlayer(gamePlayer)) {
                        point.removePlayer(gamePlayer);
                    }
                }

            }
        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player p = (Player) event.getEntity();
        GamePlayer gamePlayer = game.getGamePlayer(p);
        if (gamePlayer == null) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(event.getDamage() * 0.25); // reduce fall damage
        }

        if (p.getHealth() - event.getFinalDamage() < 1) {
            // TODO: may have to cancel event, but setting damage to zero hopefully keeps the sound effect
            event.setDamage(0);
            gamePlayer.die();
        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player p = (Player) event.getEntity();
        GamePlayer gamePlayer = game.getGamePlayer(p);
        if (gamePlayer == null) {
            return;
        }

        Entity entityDamager = event.getDamager();
        GamePlayer damager = null;
        if (entityDamager instanceof Player) {
            damager = game.getGamePlayer((Player) entityDamager);
        } else if (entityDamager instanceof Projectile) {
            ProjectileSource source = ((Projectile) entityDamager).getShooter();
            if (source instanceof Player) {
                damager = game.getGamePlayer((Player) source);
            }

        }

        if (damager == null) {
            return;
        }

        // cancel damage if it's coming from a teammate who is a different player
        if (damager.getTeam() == gamePlayer.getTeam() && damager != gamePlayer) {
            event.setCancelled(true);
        } else {
            if (entityDamager instanceof Firework) {
                Location fireworkLocation = entityDamager.getLocation();
                Location playerLocation = p.getLocation();
                Vector vector = playerLocation.subtract(fireworkLocation).toVector();
                double distance = vector.lengthSquared();
                double power = distance != 0 ? Math.min(5, 75 / distance) : 5; // max power is 5
                p.setVelocity(p.getVelocity().add(vector.multiply(power)));
            }
        }

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {

            Player p = event.getPlayer();
            GamePlayer gamePlayer = game.getGamePlayer(p);
            if (gamePlayer == null || !gamePlayer.isSpectating()) {
                return;
            }

            event.setCancelled(true);

        } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            onPlayerMove(event); // move event doesn't seem to be called automatically on plugin teleport
        }

    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        GamePlayer gamePlayer = game.getGamePlayer(player);
        if (gamePlayer == null) {
            return;
        }

        EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
        if (reason == EntityRegainHealthEvent.RegainReason.SATIATED || reason == EntityRegainHealthEvent.RegainReason.REGEN) {
            event.setAmount(event.getAmount() * 0.25);
            // TODO: enable this once health packs are implemented
//            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getGamePlayer(player);
        if (gamePlayer == null) {
            return;
        }

        Team team = gamePlayer.getTeam();
        // TODO: consider escaping characters in the name
        event.setFormat(event.getFormat()
                .replaceFirst("<", (gamePlayer.isDead() ? i18n.getString("dead") + " " : "") + team.getColor().getColor1())
                .replaceFirst(">", ChatColor.RESET + ":")
        );

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getGamePlayer(player);
        if (gamePlayer == null) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        game.getTeams().forEach(team -> team.getPlayers().forEach(p -> {
            System.out.println(player + ", " + p.getPlayer() + ", " + (player.getUniqueId().equals(p.getPlayer().getUniqueId())));
        }));

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        game.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        if (game.hasPlayer(player) && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }

    }

}
