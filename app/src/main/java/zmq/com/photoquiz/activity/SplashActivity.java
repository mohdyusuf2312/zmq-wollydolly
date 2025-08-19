package zmq.com.photoquiz.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import zmq.com.photoquiz.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    public void splashClick(View view) {
        startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
    }
}
