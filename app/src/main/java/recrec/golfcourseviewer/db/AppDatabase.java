/*
 * Filename: AppDatabase.java
 * Author: Team Recursive Recursion
 * Class: AppDatabase
 *       This class extends RoomDatabase. It provides access to the Room database
 *       through Dao objects. This class is essentially a singleton class.
 * */
package recrec.golfcourseviewer.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import recrec.golfcourseviewer.db.Dao.CoursePolygonsDao;
import recrec.golfcourseviewer.db.Dao.GolfCourseDao;
import recrec.golfcourseviewer.db.Models.CoursePolygonsModel;
import recrec.golfcourseviewer.db.Models.GolfCourseModel;

@Database(entities = {GolfCourseModel.class, CoursePolygonsModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract CoursePolygonsDao coursePolygonsModel();
    public abstract GolfCourseDao golfCourseModel();

    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *      Creates an instance of the Room database if it does not exist
     *      and returns it. Singleton pattern
     *
     *      getInMemoryDatabase(Context): AppDatabase
     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public static AppDatabase getInMemoryDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                    AppDatabase.class)
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    /*+++++++++++++++++++++++++++++++++++++
     *      destroyInstance(): void
     +++++++++++++++++++++++++++++++++++++*/
    public static void destroyInstance(){
        INSTANCE = null;
    }
}
