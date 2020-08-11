package arena.capturePoint;

import com.sk89q.worldedit.regions.Region;

public class CapturePoint {

    private final long capTime;
    private final Region region;

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
