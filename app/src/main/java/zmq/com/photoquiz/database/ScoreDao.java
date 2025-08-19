package zmq.com.photoquiz.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by zmq181 on 15/4/19.
 */

@Dao
public interface ScoreDao {

    @Query("SELECT * FROM Score")
    List<Score> getAll();

    @Insert
    void insertAll(Score score);
}
