package map;

import map.capturePoint.CapturePoint;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map implements ConfigurationSerializable {

    private static final List<Map> maps = new ArrayList<>();
    private static final java.util.Map<Player, Map> mapsInModification = new HashMap<>();

    private final Region region;
    private final List<CapturePoint> capturePoints;
    private final List<List<Region>> spawnRegions;
    private final String name;
    private final int targetScore; // default target score for map, can be changed for a game

    public Map(Region region, String name, int teams, int targetScore) {
        this.region = region;
        this.name = name;
        this.targetScore = targetScore;
        capturePoints = new ArrayList<>();
        spawnRegions = new ArrayList<>();

        for (int i = 0; i < teams; i++) {
            spawnRegions.add(new ArrayList<>());
        }

    }

    public static void removeMap(Map map) {
        maps.remove(map);
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

    public static void startModifyingMap(Map map, Player player) {
        maps.remove(map);
        mapsInModification.put(player, map);
    }

    public static Map removeMapInModification(Player player) {
        return mapsInModification.remove(player);
    }

    public static void completeMap(Player player) {
        maps.add(mapsInModification.remove(player));
    }

    public static Map getMapInModification(Player player) {
        return mapsInModification.get(player);
    }

    public int getTargetScore() {
        return targetScore;
    }

    @Override
    public java.util.Map<String, Object> serialize() {

        java.util.Map<String, Object> map = new HashMap<>();
        map.put("region", region);
        map.put("capturePoints", capturePoints);
        map.put("spawnRegions", spawnRegions);
        map.put("name", name);
        map.put("targetScore", targetScore);

        return map;

    }

    public Map(java.util.Map<String, Object> data) {

        region = (Region) data.get("region");
        capturePoints = (List<CapturePoint>) data.get("capturePoints");
        spawnRegions = (List<List<Region>>) data.get("spawnRegions");
        name = (String) data.get("name");
        targetScore = (int) data.get("targetScore");

    }


}
