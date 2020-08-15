package com.virtualparticle.mc.mckoth;

import I18n.I18n;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import commands.CommandKothMap;
import game.Game;
import game.GameManager;
import game.listeners.GamePlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class McKoth extends JavaPlugin {

    private static McKoth plugin;
    private static Logger logger;
    private static final I18n i18n = I18n.getInstance();

    private WorldEditPlugin worldEdit;
    private GameManager gameManager;

    @Override
    public void onEnable() {

        plugin = this;
        logger = getLogger();
        i18n.setPlugin(plugin);
        i18n.createBundle();

        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null || !worldEdit.isEnabled()) {
            log(Level.SEVERE, i18n.getString("dependencyNotFound", "WorldEdit"));
            log(Level.SEVERE, i18n.getString("fatalError"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        gameManager = new GameManager();
        registerCommands();

    }

    @Override
    public void onDisable() {

    }

    public static McKoth getPlugin() {
        return plugin;
    }

    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public void log(Level level, String message) {
        logger.log(level, message);
    }

    public void log(String message) {
        log(Level.INFO, message);
    }

    private void registerCommands() {

        new CommandKothMap();

    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
