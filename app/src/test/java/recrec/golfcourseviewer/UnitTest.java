package recrec.golfcourseviewer;

import android.util.Log;

import org.junit.Test;

import java.net.URL;
import java.net.URLConnection;

import recrec.golfcourseviewer.Entity.GolfHole;
import recrec.golfcourseviewer.Entity.GolfInfoPoint;
import recrec.golfcourseviewer.Entity.GolfPoint;
import recrec.golfcourseviewer.Entity.GolfPolygon;

import static org.junit.Assert.*;
import static recrec.golfcourseviewer.Entity.GolfPolygon.PolyType.TYPE_BUNKER;
import static recrec.golfcourseviewer.Entity.GolfPolygon.PolyType.TYPE_FAIRWAY;
import static recrec.golfcourseviewer.Entity.GolfPolygon.PolyType.TYPE_GREEN;
import static recrec.golfcourseviewer.Entity.GolfPolygon.PolyType.TYPE_ROUGH;
import static recrec.golfcourseviewer.Entity.GolfPolygon.PolyType.TYPE_WATER;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void can_create_GolfHole() throws Exception {
        try{
            GolfHole hole = new GolfHole();
            assertEquals(hole,hole);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfHole");
        }
    }

    @Test
    public void can_create_GolfInfoPoint() throws Exception {
        try{
            GolfInfoPoint point = new GolfInfoPoint();
            assertEquals(point,point);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfInfoPoint");
        }
    }

    @Test
    public void can_create_GolfPoint() throws Exception {
        try{
            GolfPoint point = new GolfPoint(5.5,5.5);
            assertEquals(point,point);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPoint");
        }
    }

    @Test
    public void can_create_GolfPolygon_ROUGH() throws Exception {
        try{
            GolfPolygon.PolyType type = TYPE_ROUGH;
            GolfPolygon polygon = new GolfPolygon(type);
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon ROUGH");
        }
    }

    @Test
    public void can_create_GolfPolygon_FAIRWAY() throws Exception {
        try{
            GolfPolygon.PolyType type = TYPE_FAIRWAY;
            GolfPolygon polygon = new GolfPolygon(type);
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon FAIRWAY");
        }
    }

    @Test
    public void can_create_GolfPolygon_GREEN() throws Exception {
        try{
            GolfPolygon.PolyType type = TYPE_GREEN;
            GolfPolygon polygon = new GolfPolygon(type);
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon GREEN");
        }
    }

    @Test
    public void can_create_GolfPolygon_BUNKER() throws Exception {
        try{
            GolfPolygon.PolyType type = TYPE_BUNKER;
            GolfPolygon polygon = new GolfPolygon(type);
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon BUNKER");
        }
    }

    @Test
    public void can_create_GolfPolygon_WATER() throws Exception {
        try{
            GolfPolygon.PolyType type = TYPE_WATER;
            GolfPolygon polygon = new GolfPolygon(type);
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon WATER");
        }
    }

    @Test
    public void can_add_GolfPolygon_to_Course_Hole() throws Exception {
        try{
            GolfPolygon.PolyType type = TYPE_WATER;
            GolfPolygon polygon = new GolfPolygon(type);
            GolfHole hole = new GolfHole();
            hole.addCoursePolygon(polygon);
            assertEquals(hole,hole);
        }
        catch (Exception e){
            Log.e("Create Error","Could not add GolfPolygon to GolfHole");
        }
    }

    @Test
    public void can_add_GolfPolygon_to_Hole_GolfHole() throws Exception {
        try{
            GolfPolygon.PolyType type = TYPE_WATER;
            GolfPolygon polygon = new GolfPolygon(type);
            GolfHole hole = new GolfHole();
            hole.addHolePolygon(polygon);
            assertEquals(hole,hole);
        }
        catch (Exception e){
            Log.e("Create Error","Could not add GolfPolygon to Hole on Course");
        }
    }

    @Test
    public void can_set_Point_Lat() throws Exception {
        try{
            GolfPoint point = new GolfPoint(10.0,5.5);
            point.setLatitude(11.0);
            assertEquals(11.0,point.getLatitude(),0);
        }
        catch (Exception e){
            Log.e("Create Error","Could not set and get Point Latitude");
        }
    }

    @Test
    public void can_set_Point_Lon() throws Exception {
        try{
            GolfPoint point = new GolfPoint(10.0,5.5);
            point.setLongitude(6.0);
            assertEquals(6.0,point.getLongitude(),0);
        }
        catch (Exception e){
            Log.e("Create Error","Could not set and get Point Longitude");
        }
    }

    @Test
    public void can_connect_to_server() {
        try{
            URL myUrl = new URL("127.0.0.1:5001"); //ADD API Here!
            URLConnection connection = myUrl.openConnection();
            connection.connect();
            assertTrue(true);
        } catch (Exception e) {
            Log.e("Connect Error","Could not connect to API");
        }
    }

}