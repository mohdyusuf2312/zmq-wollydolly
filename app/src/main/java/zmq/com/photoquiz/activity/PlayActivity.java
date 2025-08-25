package zmq.com.photoquiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import zmq.com.photoquiz.R;
import zmq.com.photoquiz.canvas.BaseSurface;
import zmq.com.photoquiz.canvas.GameView;
import zmq.com.photoquiz.utility.AudioPlayer;

public class PlayActivity extends AppCompatActivity {

    private GameView quizView;
    private BaseSurface baseSurface;
    public boolean isRecreate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Create the GameView
        quizView = new GameView(this);
        setContentView(quizView);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onResume() {
        super.onResume();
        // Start background music when resuming
        baseSurface = (BaseSurface) findViewById(123);

        if(baseSurface != null) {
            if (isRecreate && baseSurface.gameThread == null) {
                baseSurface.setHolder();
                quizView.timer_running = true;
                quizView.startTimer();
                quizView.thread.start();
                AudioPlayer.playBackground(this, R.raw.background);
            }
        }
        else {
            System.out.println("BaseSurface is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioPlayer.stopSound();
        if (quizView != null) {
            quizView.timer_running = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRecreate=true;
        Log.d("MyTag", "" + getClass().getSimpleName() + " onStop.........");
    }
}
