package com.virtualparticle.mc.mckoth;

import I18n.I18n;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import commands.CommandKoth;
import commands.CommandKothMap;
import game.Game;
import game.GameManager;
import game.listeners.GamePlayerListener;
import map.Map;
import map.capturePoint.CapturePoint;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class McKoth extends JavaPlugin {

    private static McKoth plugin;
    // TODO: make all references to this static
    private static final Logger logger = Logger.getLogger("McKoth");
    private static final I18n i18n = I18n.getInstance();

    private WorldEditPlugin worldEdit;
    private GameManager gameManager;

    @Override
    public void onEnable() {

        plugin = this;
        gameManager = new GameManager();

        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null || !worldEdit.isEnabled()) {
            log(Level.SEVERE, i18n.getString("dependencyNotFound", "WorldEdit"));
            log(Level.SEVERE, i18n.getString("fatalError"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        serializeClasses();
        registerCommands();

        FileConfiguration maps = getConfigFile("maps.yml");
        for (String key : maps.getKeys(false)) {

            Map map = maps.getObject(key, Map.class);
            if (map == null) {
                log(i18n.getString("errorLoadingMap", key));
            } else {
                Map.getMaps().add(map);
                log(i18n.getString("loadedMap", map.getName()));
            }

        }


    }

    @Override
    public void onDisable() {

        FileConfiguration maps = getConfigFile("maps.yml");
        for (Map map : Map.getMaps()) {
            maps.set(map.getName(), map);
        }

        try {
            maps.save(new File(getDataFolder(), "maps.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static McKoth getPlugin() {
        return plugin;
    }

    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    private void registerCommands() {

        // TODO: see if there's a better way to do this
        new CommandKothMap();
        new CommandKoth();

    }

    private void serializeClasses() {

        ConfigurationSerialization.registerClass(Map.class);
        ConfigurationSerialization.registerClass(SerializableCuboidRegion.class);
        ConfigurationSerialization.registerClass(CapturePoint.class);

    }

    private FileConfiguration getConfigFile(String name) {
        return YamlConfiguration.loadConfiguration(new File(getDataFolder(), name));
    }

    public GameManager getGameManager() {
        return gameManager;
    }

}
