package utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;

public class Utils {

    private static final double GAMMA = 0.5772156649; // Euler–Mascheroni constant (for approximating harmonic series)

    public static double harmonicApproximation(int n) {
        if (n == 0) {
            return 0;
        }
        float f = (float) n;
        return Math.log(f) + GAMMA + (1 / (2 * f)) - (1 / (12 * f * f));
    }

    public static boolean insideRegion(Location location, Region region) {
        return region.contains(BlockVector3.at(location.getX(), location.getY(), location.getZ()));
    }

}
