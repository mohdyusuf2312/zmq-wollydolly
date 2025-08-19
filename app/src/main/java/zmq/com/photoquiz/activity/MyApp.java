package zmq.com.photoquiz.activity;

import android.app.Application;
import android.arch.persistence.room.Room;

import zmq.com.photoquiz.database.MyDatabase;

/**
 * Created by zmq181 on 15/4/19.
 */

public class MyApp extends Application {

    private static MyDatabase myDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        myDatabase = Room.databaseBuilder(this,MyDatabase.class,"Quiz").allowMainThreadQueries().build();
    }

    public static MyDatabase getDatabaseInstance(){
        return myDatabase;
    }
}
