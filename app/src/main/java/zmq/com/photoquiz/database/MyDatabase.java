package zmq.com.photoquiz.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by zmq181 on 15/4/19.
 */
@Database(entities = {Score.class} , version = 1)
public abstract class MyDatabase extends RoomDatabase{
    public abstract ScoreDao getScoreDao();
}

