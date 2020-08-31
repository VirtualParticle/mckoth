package game.timer;

import com.virtualparticle.mc.mckoth.McKoth;
import game.timer.events.TimerExpireEvent;
import map.events.PointCaptureEvent;

public class Timer implements Runnable {

    protected static float DEFAULT_INTERVAL = 1;

    protected boolean paused = true;
    protected float originalTime;
    protected float time;
    protected float interval;
    private boolean expired;

    public Timer(float time) {
        this(time, DEFAULT_INTERVAL);
    }

    public Timer(float time, float interval) {
        this.originalTime = time;
        this.time = time;
        this.interval = interval;
        expired = false;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void reset() {
        time = originalTime;
        expired = false;
//        setPaused(true);
    }

    @Override
    public void run() {

        if (time > 0 && !paused) {
            time -= interval;
        } else if (time <= 0) { //&& !expired){
            expired = true;
            McKoth.getPlugin().getServer().getPluginManager().callEvent(new TimerExpireEvent(this));
        }

    }

    public float getTime() {
        return time;
    }

    public String getTimeString() {
        int minutes = (int) time / 60;
        int seconds = (int) time % 60;
        String s = minutes + ":";
        if (seconds < 10) {
            s += "0";
        }
        return s + seconds;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
