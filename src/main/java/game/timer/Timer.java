package game.timer;

public class Timer implements Runnable {

    protected static float DEFAULT_INTERVAL = 1;

    protected boolean paused = true;
    protected float originalTime;
    protected float time;
    protected float interval;

    public Timer(float time) {
        this(time, DEFAULT_INTERVAL);
    }

    public Timer(float time, float interval) {
        this.originalTime = time;
        this.time = time;
        this.interval = interval;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void reset() {
        time = originalTime;
//        setPaused(true);
    }

    @Override
    public void run() {

        if (time > 0 && !paused) {
            time -= interval;
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
