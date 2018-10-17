/*----------------------------------------------------------------------------
 *   Filename : Element.java
 *   Author : Team Recursive Recursion
 *   Class : Element
 *
 *       The Element class is a model used to store information that needs to
 *       be drawn on the map. It only has attributes and getter functions
 *       for each attribute.
 *----------------------------------------------------------------------------*/
package recrec.golfcourseviewer.Requests.Response;

public class Element {
    private String geoJson;
    private String zoneID;
    private Zone zone;
    private int elementType;
    private int classType;
    private String info;


    public String getGeoJson() {
        return geoJson;
    }

    public String getZoneID() {
        return zoneID;
    }

    public Zone getZone() {
        return zone;
    }

    public int getElementType() {
        return elementType;
    }


    public int getClassType() {
        return classType;
    }

    public String getInfo() {
        return info;
    }

}
