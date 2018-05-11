/*
 * Filename: CoursePolygonsDao.java
 * Author: Team Recursive Recursion
 * Class: CoursePolygonsDao
 *       This is a public interface used by the Room Architecture. Any
 *       queries that needs to be done on the database needs to be done through
 *       a Dao object.
 * */
package recrec.golfcourseviewer.db.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import recrec.golfcourseviewer.db.Models.CoursePolygonsModel;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CoursePolygonsDao {
    @Insert(onConflict = IGNORE)
    void insertCoursePolygon(CoursePolygonsModel coursePolygonsModel);

    @Update(onConflict = REPLACE)
    void updateCoursePolygon(CoursePolygonsModel coursePolygonsModel);


}
