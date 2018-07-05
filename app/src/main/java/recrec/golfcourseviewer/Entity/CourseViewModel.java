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

import recrec.golfcourseviewer.Requests.Response.Course;
import recrec.golfcourseviewer.Requests.Response.Hole;
import recrec.golfcourseviewer.Requests.Response.PolygonElement;
import recrec.golfcourseviewer.db.AppDatabase;

public class CourseViewModel extends AndroidViewModel {

    public MutableLiveData<List<Course>> courses = new MutableLiveData<>();
    public MutableLiveData<List<Hole>> holes = new MutableLiveData<>();
    public MutableLiveData<String> courseID = new MutableLiveData<>();
    public MutableLiveData<String> holeID = new MutableLiveData<>();

    public MutableLiveData<Boolean> holeCallResponded = new MutableLiveData<>();
    public MutableLiveData<Boolean> courseCallResponded = new MutableLiveData<>();
    public MutableLiveData<List<PolygonElement>> holesPolygons = new MutableLiveData<>();

    public AppDatabase db;

    public CourseViewModel(@NonNull Application application) {
        super(application);
    }

}
