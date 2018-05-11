/*
 * Filename: CoursePolygonsModel.java
 * Author: Team Recursive Recursion
 * Class: CoursePolygonsModel
 *       This class is used by the Room Architecture to implement the a
 *       database table with the specified attributes.
 * */
package recrec.golfcourseviewer.db.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class CoursePolygonsModel {
    @PrimaryKey
    @NonNull
    public String PolygonId;

    @NonNull
    public byte[] PolygonRaw;
}
