package game.listeners;

import I18n.I18n;
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
    private final I18n i18n = I18n.getInstance();

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

            // this means the game can only be won no one else has captime and all points are owned, not great for more than one point
            if (game.getActiveCapturePoints().stream().noneMatch(point -> point.getControllingTeam() != team)) {
                if (game.getActiveCapturePoints().stream().noneMatch(point -> point.getTeamWithCaptime() != null)) {
                    game.endRound(team);
                }
            }

        }



    }

}
