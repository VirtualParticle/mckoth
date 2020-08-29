package map.capturePoint;

import com.virtualparticle.mc.mckoth.McKoth;
import game.Game;
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
    private final String name;
    private final Game game;
    private final CaptureTimer timer;

    private final List<GamePlayer> capturingPlayers;
    private final List<GamePlayer> opposingPlayers;

    public ActiveCapturePoint(CapturePoint capturePoint, long capTime, String name, Game game) {
        this.capturePoint = capturePoint;
        this.name = name;
        this.game = game;
        this.plugin = McKoth.getPlugin();
        this.scheduler = plugin.getServer().getScheduler();
        this.capturingPlayers = new ArrayList<>();
        this.opposingPlayers = new ArrayList<>();

        timer = new CaptureTimer((float) capTime / TICKS, this, game);
        timer.setPaused(true);
        // TODO: maybe use the delay to keep points from being captured early (could do that another way)
        scheduler.scheduleSyncRepeatingTask(plugin, timer, 0, TICKS);

    }

    public CapturePoint getCapturePoint() {
        return capturePoint;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        capturingPlayers.clear();
        opposingPlayers.clear();
        timer.reset();
    }

    public void setPaused(boolean paused) {
        timer.setPaused(paused);
    }

    public void addPlayer(GamePlayer player) {
        if ((player.getTeam() == timer.getCapturingTeam() || timer.getCapturingTeam() == null) && player.getTeam() != timer.getControllingTeam()) {
            capturingPlayers.add(player);
            timer.setPaused(false);
            if (opposingPlayers.size() == 0) {
                timer.setCapturingTeam(player.getTeam(), capturingPlayers.size());
            }
        } else {
            timer.setPaused(true);
            opposingPlayers.add(player);
        }
    }

    public void removePlayer(GamePlayer player) {
        System.out.println("capturing team is " + timer.getCapturingTeam() + " player team is " + player.getTeam());
        if (timer.getCapturingTeam() == null) {
            capturingPlayers.remove(player);
            opposingPlayers.remove(player);
        } else if (player.getTeam() == timer.getCapturingTeam()) {
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
        System.out.println("capturing: " + capturingPlayers.contains(player) + " opposing: " + opposingPlayers.contains(player));
    }

    public boolean containsPlayer(GamePlayer player) {
        return capturingPlayers.contains(player) || opposingPlayers.contains(player);
    }

    public String getProgressBar() {
        return timer.getProgressBar();
    }

}
