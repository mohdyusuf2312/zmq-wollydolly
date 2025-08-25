package zmq.com.photoquiz.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import zmq.com.photoquiz.R;
import zmq.com.photoquiz.databinding.MenuBinding;
import zmq.com.photoquiz.utility.GlobalVariables;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private MenuBinding menuBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);
        menuBinding.play.setOnClickListener(this);
        menuBinding.score.setOnClickListener(this);
        menuBinding.instruction.setOnClickListener(this);
        menuBinding.about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.play:
                printScreenInfo();
                startActivity(new Intent(this,PlayActivity.class));
                break;

            case  R.id.score:
                startActivity(new Intent(this,ScoreActivity.class));
                break;

            case  R.id.instruction:
                startActivity(new Intent(this,InstructionActivity.class));
                break;

            case  R.id.about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        }
    }

    void printScreenInfo() {
        GlobalVariables.getResource = getResources();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        GlobalVariables.xScale_factor = (float) (metrics.widthPixels / 480.0);
        GlobalVariables.yScale_factor = (float) (metrics.heightPixels / 800.0);
        GlobalVariables.width = metrics.widthPixels;
        GlobalVariables.height = metrics.heightPixels;
        GlobalVariables.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        GlobalVariables.textFont = 40*GlobalVariables.xScale_factor;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    }
}
