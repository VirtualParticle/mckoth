package game.timer;

import com.virtualparticle.mc.mckoth.McKoth;
import org.bukkit.Bukkit;

public class CountdownTimer extends Timer {

    private final CountdownRunnable runnable;
    private int task;

    private CountdownTimer(CountdownRunnable runnable, long seconds) {
        super(seconds + 1, 1);
        this.runnable = runnable;
        paused = false;
    }

    public static CountdownTimer createCountdownTimer(CountdownRunnable runnable, long seconds) {
        CountdownTimer timer = new CountdownTimer(runnable, seconds);
        timer.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(McKoth.getPlugin(), timer, 0, 20);
        return timer;
    }

    @Override
    public void run() {
        super.run();
        runnable.run((long) time);
        if (time == 0) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

}
