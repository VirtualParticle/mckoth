package map;

import map.capturePoint.CapturePoint;
import com.sk89q.worldedit.regions.Region;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private static final List<Map> maps = new ArrayList<>();

    private final Region region;
    private final List<CapturePoint> capturePoints;
    private final List<List<Region>> spawnRegions;
    private final String name;

    public Map(Region region, String name, List<CapturePoint> capturePoints, List<List<Region>> spawnRegions) {
        this.region = region;
        this.name = name;
        this.capturePoints = capturePoints;
        this.spawnRegions = spawnRegions;
    }

    public Region getRegion() {
        return region;
    }

    public List<CapturePoint> getCapturePoints() {
        return capturePoints;
    }

    public boolean addCapturePoint(CapturePoint capturePoint) {
        if (capturePoints.contains(capturePoint)) {
            return false;
        }
        capturePoints.add(capturePoint);
        return true;
    }

    public static List<Map> getMaps() {
        return maps;
    }

    public String getName() {
        return name;
    }

    public static Map getMapByName(String name) {
        return maps.stream().filter(map -> map.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<List<Region>> getSpawnRegions() {
        return spawnRegions;
    }

}
