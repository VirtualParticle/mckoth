package game.listeners;

import arena.capturePoint.ActiveCapturePoint;
import arena.Map;
import game.Game;
import game.GamePlayer;
import game.Team;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import utils.Utils;

public class GamePlayerListener implements Listener {

    private final Game game;

    public GamePlayerListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        if (!game.isActive()) {
            return;
        }

        Player p = e.getPlayer();
        GamePlayer gamePlayer = game.getGamePlayer(p);
        if (gamePlayer == null) {
            return;
        }

        Location locationFrom = e.getFrom();
        Location locationTo = e.getTo();

        /* TODO: this might cause lag, so consider doing this on a regular interval instead of on event.
            It is also possible to implement a cool-down on this so it only runs it every-so-often */

        Map map = game.getMap();
        if (Utils.insideRegion(locationTo, map.getRegion())) {
            for (ActiveCapturePoint point : game.getActiveCapturePoints()) {

                if (Utils.insideRegion(locationTo, point.getCapturePoint().getRegion())
                        || Utils.insideRegion(locationFrom, point.getCapturePoint().getRegion())) {
                    if (Utils.insideRegion(locationTo, point.getCapturePoint().getRegion())) {
                        point.addPlayer(gamePlayer);
                    } else {
                        point.removePlayer(gamePlayer);
                    }
                }

            }
        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }

        if (!game.isActive()) {
            return;
        }

        Player p = (Player) e.getEntity();
        GamePlayer gamePlayer = game.getGamePlayer(p);
        if (gamePlayer == null) {
            return;
        }

        // TODO: may have to cancel event, but setting damage to zero hopefully keeps the sound effect
        e.setDamage(0);
        gamePlayer.regenHealth();

    }

}
