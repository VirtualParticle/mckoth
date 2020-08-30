package game.timer;

import com.virtualparticle.mc.mckoth.McKoth;
import org.bukkit.Bukkit;

public class CountdownTimer extends Timer {

    private final CountdownRunnable runnable;
    private int task;

    private CountdownTimer(CountdownRunnable runnable, long seconds) {
        super(seconds, 1);
        this.runnable = runnable;
    }

    @Override
    public void run() {
        super.run();
        runnable.run((long) time);
        if (time == 0) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

    public static int createCountdownTimer(CountdownRunnable runnable, long seconds) {
        CountdownTimer timer =  new CountdownTimer(runnable, seconds);
        return timer.task = Bukkit.getScheduler().scheduleSyncDelayedTask(McKoth.getPlugin(), timer);
    }

}
