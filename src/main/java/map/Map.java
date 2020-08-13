package map;

import map.capturePoint.CapturePoint;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {

    private static final List<Map> maps = new ArrayList<>();
    private static final java.util.Map<Player, Map> mapsInCreation = new HashMap<>();

    private final Region region;
    private final List<CapturePoint> capturePoints;
    private final List<List<Region>> spawnRegions;
    private final String name;

    public Map(Region region, String name, List<CapturePoint> capturePoints, List<List<Region>> spawnRegions, int teams) {
        this.region = region;
        this.name = name;
        this.capturePoints = capturePoints;
        this.spawnRegions = spawnRegions;

        this.spawnRegions.add(new ArrayList<>());
        this.spawnRegions.add(new ArrayList<>());
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

    public void addSpawnRegion(Region region, int team) {
        spawnRegions.get(team).add(region);
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

    public void startCreatingMap(Map map, Player player) {
        mapsInCreation.put(player, map);
    }

    public Map removeMapInCreation(Player player) {
        return mapsInCreation.remove(player);
    }

    public void completeMap(Player player) {
        maps.add(mapsInCreation.remove(player));
    }

}
