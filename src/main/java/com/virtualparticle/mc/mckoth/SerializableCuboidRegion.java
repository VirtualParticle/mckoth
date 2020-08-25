package com.virtualparticle.mc.mckoth;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SerializableCuboidRegion extends CuboidRegion implements ConfigurationSerializable {

    public SerializableCuboidRegion(CuboidRegion region) {
        super(region.getWorld(), region.getPos1(), region.getPos2());
    }

    public SerializableCuboidRegion(Map<String, Object> map) {
        super(BukkitAdapter.adapt(Bukkit.getWorld(UUID.fromString((String) map.get("world")))),
                BukkitAdapter.asBlockVector((Location) map.get("pos1")),
                BukkitAdapter.asBlockVector((Location) map.get("pos2")));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();
        org.bukkit.World bukkitWorld = BukkitAdapter.adapt(world);
        map.put("world", bukkitWorld.getUID().toString());
        map.put("pos1", BukkitAdapter.adapt(bukkitWorld, getPos1()));
        map.put("pos2", BukkitAdapter.adapt(bukkitWorld, getPos2()));

        return map;

    }

}
