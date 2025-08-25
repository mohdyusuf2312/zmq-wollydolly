package zmq.com.photoquiz.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Created by zmq181 on 15/4/19.
 * Recreated by Mohd Yusuf on 25/Aug/2025.
 */

@Dao
public interface ScoreDao {

    @Query("SELECT * FROM Score")
    List<Score> getAll();

    @Insert
    void insertAll(Score score);
}
