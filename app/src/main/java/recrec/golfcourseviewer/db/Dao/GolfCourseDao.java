/*
* Filename: GolfCourseDao.java
* Author: Team Recursive Recursion
* Class: GolfCourseDao
*       This is a public interface used by the Room Architecture. Any
*       queries that needs to be done on the database needs to be done through
*       a Dao object.
* */
package recrec.golfcourseviewer.db.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import recrec.golfcourseviewer.db.Models.GolfCourseModel;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface GolfCourseDao {
    @Insert(onConflict = IGNORE)
    void insertGolfCourse(GolfCourseModel golfCourseModel);

    @Query("SELECT * FROM GolfCourseModel")
    LiveData<List<GolfCourseModel>> getAllCourses();
}
