package map.capturePoint;

import com.sk89q.worldedit.regions.Region;

public class CapturePoint {

    private static final long DEFAULT_CAPTIME = 60 * 3;

    private final long capTime;
    private final Region region;

    public CapturePoint(Region region) {
        this(region, DEFAULT_CAPTIME);
    }

    public CapturePoint(Region region, long capTime) {
        this.region = region;
        this.capTime = capTime;
    }

    public ActiveCapturePoint getActiveCapturePoint(String name) {
        return new ActiveCapturePoint(this, capTime, name);
    }

    public ActiveCapturePoint getActiveCapturePoint() {
        return getActiveCapturePoint(null);
    }

    public Region getRegion() {
        return region;
    }

}
