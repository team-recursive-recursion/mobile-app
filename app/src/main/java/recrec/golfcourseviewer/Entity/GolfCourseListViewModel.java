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
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import recrec.golfcourseviewer.db.AppDatabase;
import recrec.golfcourseviewer.db.Models.GolfCourseModel;

public class GolfCourseListViewModel extends AndroidViewModel {

    public LiveData<List<GolfCourseModel>> courses;
    public AppDatabase db;

    public GolfCourseListViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInMemoryDatabase(application);
        courses = db.golfCourseModel().getAllCourses();
    }

}
