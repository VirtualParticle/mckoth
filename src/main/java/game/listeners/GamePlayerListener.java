package game.listeners;

import map.capturePoint.ActiveCapturePoint;
import map.Map;
import game.Game;
import game.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import utils.MathUtils;

public class GamePlayerListener implements Listener {

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

        /* TODO: this might cause lag, so consider doing this on a regular interval instead of on event.
            It is also possible to implement a cool-down on this so it only runs it every-so-often */

        Map map = game.getMap();
        if (MathUtils.insideRegion(locationTo, map.getRegion())) {
            for (ActiveCapturePoint point : game.getActiveCapturePoints()) {

                if (MathUtils.insideRegion(locationTo, point.getCapturePoint().getRegion())
                        || MathUtils.insideRegion(locationFrom, point.getCapturePoint().getRegion())) {
                    if (MathUtils.insideRegion(locationTo, point.getCapturePoint().getRegion()) && !point.containsPlayer(gamePlayer)) {
                        System.out.println("adding player");
                        point.addPlayer(gamePlayer);
                    } else if (!MathUtils.insideRegion(locationTo, point.getCapturePoint().getRegion()) && point.containsPlayer(gamePlayer)) {
                        System.out.println("removing player");
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

        }

    }

}
