package game.listeners;

import arena.capturePoint.ActiveCapturePoint;
import arena.Map;
import com.virtualparticle.mc.mckoth.McKoth;
import game.Game;
import game.GamePlayer;
import game.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import utils.Utils;

public class GamePlayerListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();
        Location locationFrom = e.getFrom();
        Location locationTo = e.getTo();

        /* TODO: this might cause lag, so consider doing this on a regular interval instead of on event.
            It is also possible to implement a cool-down on this so it only runs it every-so-often */

        for (Game game : McKoth.getPlugin().getGames()) {
            Map map = game.getMap();
            if (Utils.insideRegion(locationTo, map.getRegion())) {
                for (ActiveCapturePoint point : game.getActiveCapturePoints()) {

                    if (Utils.insideRegion(locationTo, point.getCapturePoint().getRegion())
                            || Utils.insideRegion(locationFrom, point.getCapturePoint().getRegion())) {
                        for (Team team : game.getTeams()) {
                            for (GamePlayer player : team.getPlayers()) {
                                if (player.getPlayer() == p) {
                                    if (Utils.insideRegion(locationTo, point.getCapturePoint().getRegion())) {
                                        point.addPlayer(player);
                                    } else {
                                        point.removePlayer(player);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

    }

}
