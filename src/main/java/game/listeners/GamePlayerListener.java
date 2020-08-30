package game.listeners;

import I18n.I18n;
import game.Team;
import map.capturePoint.ActiveCapturePoint;
import map.Map;
import game.Game;
import game.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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

        if (p.getHealth() - event.getFinalDamage() < 1) {
            // TODO: may have to cancel event, but setting damage to zero hopefully keeps the sound effect
            event.setDamage(0);
            gamePlayer.die();
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
            event.setCancelled(true);
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
                .replaceFirst("<", (gamePlayer.isDead() ? i18n.getString("dead") + " " : "") + team.getColor().toString())
                .replaceFirst(">", ChatColor.RESET + ":")
        );

    }
}
