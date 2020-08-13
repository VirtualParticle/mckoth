package map;

import map.capturePoint.CapturePoint;
import com.sk89q.worldedit.regions.Region;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private static final List<Map> maps = new ArrayList<>();

    private final Region region;
    private final List<CapturePoint> capturePoints;

    public Map(Region region) {
        this.region = region;
        capturePoints = new ArrayList<>();
    }

    public Region getRegion() {
        return region;
    }

    public List<CapturePoint> getCapturePoints() {
        return capturePoints;
    }

    public static List<Map> getMaps() {
        return maps;
    }

}
