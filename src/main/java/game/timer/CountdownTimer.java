package game.timer;

public class CountdownTimer extends Timer {

    private final Runnable runnable;

    public CountdownTimer(Runnable runnable, long time) {
        super(time, 1);
        this.runnable = runnable;
    }

    public static int createCountdownTimer(Runnable runnable, long time) {
        return 0;
    }

}
