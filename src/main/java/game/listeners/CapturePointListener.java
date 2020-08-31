package game.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.virtualparticle.mc.mckoth.McKoth;
import game.timer.CaptureTimer;
import map.capturePoint.ActiveCapturePoint;
import map.capturePoint.CapturePoint;
import map.events.PointCaptureEvent;
import game.Game;
import game.Team;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import utils.WorldEditUtils;

public class CapturePointListener implements Listener {

    private final Game game;

    public CapturePointListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onPointCapture(PointCaptureEvent e) {

        if (!game.isActive()) {
            return;
        }

        Team currentTeam = e.getCurrentTeam();
        Team capturingTeam = e.getCapturingTeam();
        ActiveCapturePoint capturePoint = e.getCapturePoint();

        capturePoint.setColor(capturingTeam.getColor());

        if (capturingTeam.getTimer().getTime() <= 0) {
            // this is when a team recaptures after losing the point in overtime
//            game.endRound(capturingTeam);
        }

    }

}
