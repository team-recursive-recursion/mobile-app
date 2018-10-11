package recrec.golfcourseviewer.Requests.Response;

import java.util.List;

public class Zone {
    private String zoneID;
    private String zoneName;
    private String userId;
    private String info	;
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

    public String getInfo() {
        return info;
    }

    public List<Zone> getInnerZones() {
        return innerZones;
    }

    public List<Element> getElements() {
        return elements;
    }
}
