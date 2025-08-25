package zmq.com.photoquiz.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Created by zmq181 on 15/4/19.
 * Recreated by Mohd Yusuf on 25/Aug/2025.
 */
@Database(entities = {Score.class} , version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase{
    public abstract ScoreDao getScoreDao();
}

