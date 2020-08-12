package com.virtualparticle.mc.mckoth;

import I18n.I18n;
import game.Game;
import game.listeners.GamePlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class McKoth extends JavaPlugin {

    private static McKoth plugin;
    private static I18n i18n = I18n.getInstance();

    private Logger logger;
    private List<Game> games;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        games = new ArrayList<>();
        logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static McKoth getPlugin() {
        return plugin;
    }

    public List<Game> getGames() {
        return games;
    }

    public void log(Level level, String message) {
        logger.log(level, message);
    }

    public void log(String message) {
        log(Level.INFO, message);
    }

}
