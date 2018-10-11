/*
 * Filename: GolfCourseListViewModel.java
 * Author: Team Recursive Recursion
 * Class: GolfCourseListViewModel
 *       This class extends AndroidViewModel. It is used to implement the
 *       automatic update of LiveData objects.
 * */
package recrec.golfcourseviewer.Entity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;
import recrec.golfcourseviewer.Requests.Response.Point;
import recrec.golfcourseviewer.Requests.Response.Zone;
import recrec.golfcourseviewer.Requests.Response.Element;

public class CourseViewModel extends AndroidViewModel {

    public MutableLiveData<List<Zone>> courses = new MutableLiveData<>();
    public MutableLiveData<Zone> courseZone = new MutableLiveData<>();
    public MutableLiveData<List<Zone>> holes = new MutableLiveData<>();
    public MutableLiveData<String> courseID = new MutableLiveData<>();
    public MutableLiveData<String> holeID = new MutableLiveData<>();

    public MutableLiveData<List<Zone>> zoneList = new MutableLiveData<>();
    public MutableLiveData<List<Point>> pointsPolygons = new
            MutableLiveData<>();

    public MutableLiveData<String> weatherData = new MutableLiveData<>();
    public MutableLiveData<Double> distanceToHole = new MutableLiveData<>();

    public CourseViewModel(@NonNull Application application) {
        super(application);
    }

}
