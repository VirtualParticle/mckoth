package game.timer;

import com.virtualparticle.mc.mckoth.McKoth;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class CountdownTimer extends Timer {

    private final CountdownRunnable runnable;
    private final BukkitScheduler scheduler;
    private int task;

    private CountdownTimer(CountdownRunnable runnable, long seconds) {
        super(seconds + 1, 1);
        this.runnable = runnable;
        this.scheduler = Bukkit.getScheduler();
        paused = false;
    }

    @Override
    public void run() {
        super.run();
        runnable.run((long) time);
        if (time == 0) {
            scheduler.cancelTask(task);
        }
    }

    public void cancel() {
        scheduler.cancelTask(task);
    }

    public static CountdownTimer createCountdownTimer(CountdownRunnable runnable, long seconds) {
        CountdownTimer timer = new CountdownTimer(runnable, seconds);
        timer.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(McKoth.getPlugin(), timer, 0, 20);
        return timer;
    }

}
