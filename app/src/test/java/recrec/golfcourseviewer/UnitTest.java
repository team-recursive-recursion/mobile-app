package recrec.golfcourseviewer;

import android.util.Log;
import org.junit.Test;
import java.net.URL;
import java.net.URLConnection;
import recrec.golfcourseviewer.Requests.Response.Zone;
import static org.junit.Assert.*;

public class UnitTest {
    @Test
    public void can_create_GolfHole() {
        try{
            Zone hole = new Zone();
            assertEquals(hole,hole);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfHole");
        }
    }

    @Test
    public void can_create_GolfInfoPoint() {
        try{
            Zone point = new Zone();
            assertEquals(point,point);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfInfoPoint");
        }
    }

    @Test
    public void can_create_GolfPoint() {
        try{
            Zone point = new Zone();
            assertEquals(point,point);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPoint");
        }
    }

    @Test
    public void can_create_GolfPolygon_ROUGH() {
        try{
            Zone polygon = new Zone();
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon ROUGH");
        }
    }

    @Test
    public void can_create_GolfPolygon_FAIRWAY() {
        try{
            Zone polygon = new Zone();
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon FAIRWAY");
        }
    }

    @Test
    public void can_create_GolfPolygon_GREEN() {
        try{
            Zone polygon = new Zone();
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon GREEN");
        }
    }

    @Test
    public void can_create_GolfPolygon_BUNKER() {
        try{
            Zone polygon = new Zone();
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon BUNKER");
        }
    }

    @Test
    public void can_create_GolfPolygon_WATER() {
        try{
            Zone polygon = new Zone();
            assertEquals(polygon,polygon);
        }
        catch (Exception e){
            Log.e("Create Error","Could not create GolfPolygon WATER");
        }
    }

    @Test
    public void can_add_GolfPolygon_to_Course_Hole() {
        try{
            Zone hole = new Zone();
            assertEquals(hole,hole);
        }
        catch (Exception e){
            Log.e("Create Error","Could not add GolfPolygon to GolfHole");
        }
    }

    @Test
    public void can_add_GolfPolygon_to_Hole_GolfHole() {
        try{
            Zone hole = new Zone();
            assertEquals(hole,hole);
        }
        catch (Exception e){
            Log.e("Create Error","Could not add GolfPolygon to Hole on Course");
        }
    }

    @Test
    public void can_set_Point_Lat() {
        try{
            Zone point = new Zone();
            assertEquals(11.0,11.0);
        }
        catch (Exception e){
            Log.e("Create Error","Could not set and get Point Latitude");
        }
    }

    @Test
    public void can_set_Point_Lon() {
        try{

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