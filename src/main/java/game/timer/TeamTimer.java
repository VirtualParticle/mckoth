package game.timer;

import game.Team;

public class TeamTimer extends Timer {

    private final Team team;

    public TeamTimer(Team team, float time) {
        super(time);
        this.team = team;
    }

    public TeamTimer(Team team, float time, float interval) {
        super(time, interval);
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

}
