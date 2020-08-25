package utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.virtualparticle.mc.mckoth.McKoth;
import com.virtualparticle.mc.mckoth.SerializableCuboidRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldEditUtils {

    private static final McKoth plugin = McKoth.getPlugin();
    private static final WorldEditPlugin worldEdit = plugin.getWorldEdit();

    public static Region getSelectionRegion(Player player) {

        WorldEditPlugin worldEdit = plugin.getWorldEdit();
        LocalSession session = worldEdit.getSession(player);
        Region region = null;
        try {
            region = session.getSelection(session.getSelectionWorld()).clone();
        } catch (IncompleteRegionException ignored) {

        }

        if (region instanceof CuboidRegion) {
            region = new SerializableCuboidRegion((CuboidRegion) region);
        }

        return region;

    }

}
