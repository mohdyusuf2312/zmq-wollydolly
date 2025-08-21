package zmq.com.photoquiz.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by zmq181 on 15/4/19.
 */

@Entity
public class Score {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String name;

    private int score;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
