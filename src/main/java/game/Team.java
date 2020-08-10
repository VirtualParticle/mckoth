package game;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final String name;
    private final List<GamePlayer> players;

    public Team(String name) {
        this.name = name;
        players = new ArrayList<>();
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

}
