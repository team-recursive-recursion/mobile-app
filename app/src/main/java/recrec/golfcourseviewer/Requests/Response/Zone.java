package recrec.golfcourseviewer.Requests.Response;

import java.util.List;

public class Zone {
    private String zoneID;
    private String zoneName;
    private String userId;
    private String createdAt;
    private String updatedAt;
    private String info	;
    private String parentZoneID;
    private String parentZone;
    private List<Zone> innerZones;
    private List<Element> elements;

    public String getZoneID() {
        return zoneID;
    }

    public String getZoneName() {
        return zoneName;
    }

    public String getUserId() {
        return userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getInfo() {
        return info;
    }

    public String getParentZoneID() {
        return parentZoneID;
    }

    public String getParentZone() {
        return parentZone;
    }

    public List<Zone> getInnerZones() {
        return innerZones;
    }

    public List<Element> getElements() {
        return elements;
    }
}
