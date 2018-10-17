/*----------------------------------------------------------------------------
 *   Filename : Zone.java
 *   Author : Team Recursive Recursion
 *   Class : Zone
 *
 *      The Zone class is a model used in requests to the API. It stores the
 *      information retrieved from API requests. It consists only of attributes
 *      and getter functions for each attribute.
 *----------------------------------------------------------------------------*/
package recrec.golfcourseviewer.Requests.Response;

import java.util.List;

public class Zone {
    private String zoneID;
    private String zoneName;
    private String info	;
    private List<Zone> innerZones;
    private List<Element> elements;

    public String getZoneID() {
        return zoneID;
    }

    public String getZoneName() {
        return zoneName;
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
