package com.virtualparticle.mc.mckoth;

import game.Game;
import game.listeners.GamePlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class McKoth extends JavaPlugin {

    private static McKoth plugin;

    private List<Game> games;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getServer().getPluginManager().registerEvents(new GamePlayerListener(), this);
        games = new ArrayList<>();
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

}
