package game;

import I18n.I18n;
import game.exceptions.GameJoinException;
import game.listeners.TimerListener;
import game.timer.CountdownTimer;
import map.capturePoint.ActiveCapturePoint;
import map.Map;
import com.virtualparticle.mc.mckoth.McKoth;
import game.listeners.CapturePointListener;
import game.listeners.GamePlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import utils.ChatUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {

    private final long CAPTIME = 60 * 3; // three minutes

    private final Map map;
    private final int id;
    private final List<Team> teams;
    private final List<ActiveCapturePoint> activeCapturePoints;
    private final McKoth plugin;
    private final I18n i18n = I18n.getInstance();
    private final int targetScore;
    private boolean active = false;
    private boolean frozen;
    private boolean started;

    private CountdownTimer startTimer;

    protected Game(Map map, int id) {
        this(map, id, map.getTargetScore());
    }

    protected Game(Map map, int id, int targetScore) {
        this.map = map;
        this.id = id;
        this.targetScore = targetScore;

        teams = new ArrayList<>();
        plugin = McKoth.getPlugin();
        activeCapturePoints = new ArrayList<>();
        map.getCapturePoints().forEach(capturePoint -> activeCapturePoints.add(capturePoint.getActiveCapturePoint(this)));

        plugin.getServer().getPluginManager().registerEvents(new CapturePointListener(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GamePlayerListener(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TimerListener(this), plugin);

        // TODO: this can be changed to allow more than two teams
        teams.add(new Team("BLU", CAPTIME, map.getSpawnRegions().get(0), this, TeamColor.BLUE));
        teams.add(new Team("RED", CAPTIME, map.getSpawnRegions().get(1), this, TeamColor.RED));

    }

    public GamePlayer addPlayer(Player player) throws GameJoinException {

        Team smallestTeam = teams.get(0); // assuming teams.size() > 0
        for (Team team : teams) {
            if (team.getPlayers().size() < smallestTeam.getPlayers().size()) {
                smallestTeam = team;
            }
        }

        if (smallestTeam.getPlayers().size() >= map.getMaxPlayers()) {
            throw new GameJoinException(i18n.getString("gameIsFull"));
        }

        GamePlayer gamePlayer = new GamePlayer(player, smallestTeam);
        smallestTeam.addPlayer(gamePlayer);
        gamePlayer.respawn();

        if (smallestTeam.getPlayers().size() > map.getMaxPlayers() / 2 && startTimer.getTime() > 30) {
            startTimer.setTime(30);
        }

        return gamePlayer;
    }

    public boolean removePlayer(Player player) {

        GamePlayer gamePlayer = null;
        for (Team team : teams) {
            GamePlayer gp = team.remove(player);
            gamePlayer = gp != null ? gp : gamePlayer;
        }

        if (gamePlayer != null) {
            player.setGameMode(GameMode.SPECTATOR);
            GamePlayer finalGamePlayer = gamePlayer;
            activeCapturePoints.forEach(cp -> cp.removePlayer(finalGamePlayer));
            return true;
        }

        return false;
    }

    public Map getMap() {
        return map;
    }

    public int getId() {
        return id;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<ActiveCapturePoint> getActiveCapturePoints() {
        return activeCapturePoints;
    }

    public void setup() {

        active = true; // TODO: maybe move to follow warmup, maybe not

        // TODO: change this to work with more than two teams
        Team blu = teams.get(0);
        Team red = teams.get(1);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (started) {

                StringBuilder sb = new StringBuilder();
                sb.append(blu.getColor() + (ChatColor.BOLD + blu.getTimer().getTimeString() + "    "));
                for (int i = 0; i < activeCapturePoints.size(); i++) {
                    sb.append(activeCapturePoints.get(i).getProgressBar());
                    if (i < activeCapturePoints.size() - 1) {
                        sb.append("    ");
                    }
                }
                sb.append(red.getColor() + ("    " + ChatColor.BOLD + red.getTimer().getTimeString()));
                String actionBar = sb.toString();
                teams.forEach(team -> team.getPlayers().forEach(player -> ChatUtils.sendActionBar(player.getPlayer(), actionBar)));
            }
        }, 0, 5);

        startTimer = CountdownTimer.createCountdownTimer(time -> {

            if (time > 0) {
                teams.forEach(team -> team.getPlayers().forEach(player -> {
                    ChatUtils.sendActionBar(player.getPlayer(), i18n.getString("gameStartTime", String.valueOf(time)));
                }));
            } else {
                startRound();
            }

        }, 120);

        activeCapturePoints.forEach(ActiveCapturePoint::reset);

    }

    public void startRound() {

        startTimer.cancel();

        activeCapturePoints.forEach(point -> {
            point.setPaused(true);
            point.reset();
        });

        teams.forEach(team -> {
            team.enableTimer();
            team.getTimer().setPaused(true);
            team.getTimer().reset();
            team.getPlayers().forEach(GamePlayer::respawn);
        });

        frozen = true; // make sure to wait until the players are moved to spawn to freeze
        CountdownTimer.createCountdownTimer(time -> {

            if (time > 0) {
                teams.forEach(team -> team.getPlayers().forEach(player -> {
                    ChatUtils.sendActionBar(player.getPlayer(), i18n.getString("roundStartTime", String.valueOf(time)));
                }));
            } else {
                frozen = false;
                started = true;
                activeCapturePoints.forEach(point -> {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        teams.forEach(team -> team.getPlayers().forEach(player -> {
                            player.getPlayer().sendTitle("", i18n.getString("pointIsAvailable"), 5, 20 * 3, 5);
                        }));
                        point.setPaused(false);
                    }, 20 * 15); // point is locked for the first 15 seconds
                });
            }

        }, 10);

    }

    public void endRound(Team winningTeam) {

        started = false;

        activeCapturePoints.forEach(point -> {
            point.setPaused(true);
            point.reset();
        });

        teams.forEach(team -> {
            team.disableTimer();
            team.getTimer().setPaused(true);
            team.getTimer().reset();
        });

        int score = winningTeam.incrementPoints();
        String title = "";
        if (score >= targetScore) {
            title = winningTeam.getColor() + i18n.getString("wins", winningTeam.getName());
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            team.getTimer().reset();
            sb.append(team.getColor());
            sb.append(ChatColor.BOLD);
            sb.append(team.getName()).append(": ");
            sb.append(team.getPoints());
            if (i < teams.size() - 1) {
                sb.append("   ");
            }
        }

        String msg = sb.toString();
        String finalTitle = title;
        teams.forEach(team -> team.getPlayers().forEach(player -> player.getPlayer().sendTitle(finalTitle, msg, 5, 20 * 13, 5)));

        // endgame
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (score >= targetScore) {
                endGame(winningTeam);
                plugin.getGameManager().getGames().remove(this);
            } else {
                startRound();
            }
        }, 15 * 20);

    }

    public void endGame(Team winningTeam) {
        active = false; // TODO: this might end up being moved to endRound()
        started = false;
        teams.forEach(team -> {
            team.disableTimer();
            Iterator<GamePlayer> iter = team.getPlayers().iterator();
            while (iter.hasNext()) {
                iter.next().getPlayer().setGameMode(GameMode.SPECTATOR);
                iter.remove();
            }
        });
        activeCapturePoints.forEach(capturePoint -> {
            capturePoint.disableTimer();
            capturePoint.reset();
        });
    }

    public boolean isActive() {
        return active;
    }

    public GamePlayer getGamePlayer(Player p) {
        if (p == null) {
            return null;
        }
        GamePlayer gamePlayer = null;
        for (Team team : teams) {
            for (GamePlayer player : team.getPlayers()) {
                if (player.getPlayer().getUniqueId().equals(p.getUniqueId())) {
                    gamePlayer = player;
                }
            }
        }
        return gamePlayer;
    }

    public boolean hasPlayer(Player player) {
        return teams.stream().anyMatch(team -> team.hasPlayer(player));
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean isStarted() {
        return started;
    }

}
