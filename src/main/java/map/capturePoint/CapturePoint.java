package map.capturePoint;

import com.sk89q.worldedit.regions.Region;
import game.Game;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class CapturePoint implements ConfigurationSerializable {

    private static final long DEFAULT_CAPTIME = 20 * 16; // 16 seconds

    private final long capTime;
    private final Region region;

    public CapturePoint(Region region) {
        this(region, DEFAULT_CAPTIME);
    }

    public CapturePoint(Region region, long capTime) {
        this.region = region;
        this.capTime = capTime;
    }

    public ActiveCapturePoint getActiveCapturePoint(Game game, String name) {
        return new ActiveCapturePoint(this, capTime, name, game);
    }

    public ActiveCapturePoint getActiveCapturePoint(Game game) {
        return getActiveCapturePoint(game, null);
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();
        map.put("capTime", capTime);
        map.put("region", region);

        return map;

    }

    public CapturePoint(Map<String, Object> data) {
        this((Region) data.get("region"), ((Integer) data.get("capTime")).longValue());
    }

}
