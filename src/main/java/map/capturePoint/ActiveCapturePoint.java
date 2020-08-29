package map.capturePoint;

import com.virtualparticle.mc.mckoth.McKoth;
import game.Game;
import game.GamePlayer;
import game.timer.CaptureTimer;
import org.bukkit.scheduler.BukkitScheduler;
import utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class ActiveCapturePoint {

    private static final long TICKS = 5; // how frequently to update timer in ticks

    private final McKoth plugin;
    private final BukkitScheduler scheduler;

    private final CapturePoint capturePoint;
    private final String name;
    private final Game game;
    private final CaptureTimer timer;
    private int timerTask;

    private final List<GamePlayer> players;

    public ActiveCapturePoint(CapturePoint capturePoint, long capTime, String name, Game game) {
        this.capturePoint = capturePoint;
        this.name = name;
        this.game = game;
        this.plugin = McKoth.getPlugin();
        this.scheduler = plugin.getServer().getScheduler();
        this.players = new ArrayList<>();

        timer = new CaptureTimer((float) capTime / TICKS, this, game);
        timer.setPaused(false);
        timer.updateSpeed();
        // TODO: maybe use the delay to keep points from being captured early (could do that another way)
        timerTask = scheduler.scheduleSyncRepeatingTask(plugin, timer, 0, TICKS);

    }

    public CapturePoint getCapturePoint() {
        return capturePoint;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        timer.reset();
    }

    public void setPaused(boolean paused) {
        timer.setPaused(paused);
    }

    public void addPlayer(GamePlayer player) {

        players.add(player);

        if (timer.getCapturingTeam() == null && player.getTeam() != timer.getControllingTeam()) {
            timer.setCapturingTeam(player.getTeam());
        }

        timer.updateSpeed();

    }

    public void removePlayer(GamePlayer player) {

        players.remove(player);

        if (timer.getCapturingTeam() == player.getTeam() && players.stream().noneMatch(p -> p.getTeam() == player.getTeam())) {
            if (players.size() == 0) {
                timer.setCapturingTeam(null);
            } else {
                timer.setCapturingTeam(players.get(0).getTeam());
            }
        }

        timer.updateSpeed();

    }

    public boolean containsPlayer(GamePlayer player) {
        return players.contains(player);
    }

    public String getProgressBar() {
        return timer.getProgressBar();
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void disableTimer() {
        scheduler.cancelTask(timerTask);
    }

}
