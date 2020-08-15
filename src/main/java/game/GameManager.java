package game;

import map.Map;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final List<Game> games;
    private int nextId = 0;

    public GameManager() {
        games = new ArrayList<>();
    }

    public Game createGame(Map map) {

        Game game = new Game(map, nextId++);
        games.add(game);
        return game;

    }

    public List<Game> getGames() {
        return games;
    }

    public Game getGameByMap(Map map) {
        return games.stream().filter(game -> game.getMap() == map).findFirst().orElse(null);
    }

    public Game getGameByPlayer(Player player) {
        return games.stream().filter(game -> game.hasPlayer(player)).findFirst().orElse(null);
    }

}
