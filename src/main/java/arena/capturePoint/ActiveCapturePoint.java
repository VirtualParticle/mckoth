package arena.capturePoint;

import com.virtualparticle.mc.mckoth.McKoth;
import game.GamePlayer;
import game.timer.CaptureTimer;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class ActiveCapturePoint {

    private static final long TICKS = 5; // how frequently to update timer in ticks

    private final McKoth plugin;
    private final BukkitScheduler scheduler;

    private final CapturePoint capturePoint;
    private long capTime;
    private final String name;
    private final CaptureTimer timer;

    private final List<GamePlayer> capturingPlayers;
    private final List<GamePlayer> opposingPlayers;

    public ActiveCapturePoint(CapturePoint capturePoint, long capTime, String name) {
        this.capturePoint = capturePoint;
        this.capTime = capTime;
        this.name = name;
        this.plugin = McKoth.getPlugin();
        this.scheduler = plugin.getServer().getScheduler();
        this.capturingPlayers = new ArrayList<>();
        this.opposingPlayers = new ArrayList<>();

        timer = new CaptureTimer(capTime / TICKS, this);
        timer.setPaused(true);
        // TODO: maybe use the delay to keep points from being captured early (could do that another way)
        scheduler.scheduleSyncRepeatingTask(plugin, timer, 0, TICKS);

    }

    public CapturePoint getCapturePoint() {
        return capturePoint;
    }

    public long getCapTime() {
        return capTime;
    }

    public String getName() {
        return name;
    }

    public void resetTimer() {
        timer.reset();
    }

    public void setPaused(boolean paused) {
        timer.setPaused(paused);
    }

    public void addPlayer(GamePlayer player) {
        if (player.getTeam() == timer.getCapturingTeam() || timer.getCapturingTeam() == null) {
            capturingPlayers.add(player);
            if (opposingPlayers.size() > 0) {
                timer.setCapturingTeam(player.getTeam(), capturingPlayers.size());
            }
        } else {
            timer.setPaused(true);
            opposingPlayers.add(player);
        }
    }

    public void removePlayer(GamePlayer player) {
        if (player.getTeam() == timer.getCapturingTeam()) {
            capturingPlayers.remove(player);
            timer.setCapturingTeam(player.getTeam(), capturingPlayers.size());
            if (capturingPlayers.size() == 0) {
                if (opposingPlayers.size() == 0) {
                    timer.setCapturingTeam(null, 0);
                    timer.setPaused(false);
                } else {
                    // TODO: make this work for more than two teams
                    timer.setCapturingTeam(opposingPlayers.get(0).getTeam(), opposingPlayers.size());
                    capturingPlayers.clear();
                    capturingPlayers.addAll(opposingPlayers);
                    opposingPlayers.clear();
                }
            }
        } else {
            opposingPlayers.remove(player);
            if (opposingPlayers.size() == 0) {
                timer.setCapturingTeam(timer.getCapturingTeam(), capturingPlayers.size());
                timer.setPaused(false);
            }
        }
    }

}
