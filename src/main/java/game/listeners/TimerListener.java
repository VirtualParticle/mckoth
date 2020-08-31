package game.listeners;

import game.Game;
import game.Team;
import game.timer.CaptureTimer;
import game.timer.TeamTimer;
import game.timer.Timer;
import game.timer.events.TimerExpireEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TimerListener implements Listener {

    private final Game game;

    public TimerListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onTimerExpire(TimerExpireEvent event) {

        Timer timer = event.getTimer();
        if (timer instanceof TeamTimer) {

            TeamTimer teamTimer = (TeamTimer) timer;
            Team team = teamTimer.getTeam();
            Game game = team.getGame();

            if (this.game != game) {
                return;
            }

            // this means the game can only be won if the team controls all capture points
            if (game.getActiveCapturePoints().stream().noneMatch(point -> point.getControllingTeam() != team)) {
                game.endRound(team);
            }

        }



    }

}
