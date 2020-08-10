package game.timer;

public class Timer implements Runnable {

    protected static float DEFAULT_INTERVAL = 1;

    protected boolean paused = true;
    protected long originalTime;
    protected long time;
    protected float interval;

    public Timer(long time) {
        this(time, DEFAULT_INTERVAL);
    }

    public Timer(long time, float interval) {
        this.originalTime = time;
        this.time = time;
        this.interval = interval;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void reset() {
        time = originalTime;
    }

    @Override
    public void run() {

        if (time > 0 && !paused) {
            time -= interval;
        }

    }

}
