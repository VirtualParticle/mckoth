package utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.virtualparticle.mc.mckoth.McKoth;
import org.bukkit.entity.Player;

public class WorldEditUtils {

    private static final McKoth plugin = McKoth.getPlugin();
    private static final WorldEditPlugin worldEdit = plugin.getWorldEdit();

    public static Region getSelectionRegion(Player player) {

        WorldEditPlugin worldEdit = plugin.getWorldEdit();
        LocalSession session = worldEdit.getSession(player);
        Region region = null;
        try {
            region = session.getSelection(session.getSelectionWorld());
        } catch (IncompleteRegionException ignored) {

        }

        return region;

    }

}
