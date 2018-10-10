package recrec.golfcourseviewer.Requests.Response;

class Element {
    private String geoJson;
    private String elementId;
    private String zoneID;
    private Zone zone;
    private int elementType;
    private String raw;
    private int classType;
    private String info;
    private String createdAt;
    private String updatedAt;


    public String getGeoJson() {
        return geoJson;
    }

    public String getElementId() {
        return elementId;
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

    public String getRaw() {
        return raw;
    }

    public int getClassType() {
        return classType;
    }

    public String getInfo() {
        return info;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
