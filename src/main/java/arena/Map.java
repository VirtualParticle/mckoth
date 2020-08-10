package arena;

import com.sk89q.worldedit.regions.Region;

import java.util.ArrayList;
import java.util.List;

public class Map {

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

}
