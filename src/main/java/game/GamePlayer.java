package game;

import org.bukkit.entity.Player;

public class GamePlayer {

    private Team team;
    private final Player player;

    public GamePlayer(Player player) {
        this(player, null);
    }

    public GamePlayer(Player player, Team team) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public Player getPlayer() {
        return player;
    }

}
