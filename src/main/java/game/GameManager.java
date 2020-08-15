package game;

import map.Map;

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

}
