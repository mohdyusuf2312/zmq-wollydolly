package zmq.com.photoquiz.model;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import zmq.com.photoquiz.R;
import zmq.com.photoquiz.utility.GlobalVariables;
import zmq.com.photoquiz.utility.Utility;

/**
 * Created by zmq181 on 11/4/19.
 */

public class KeyAndChar {
    private Bitmap bitmap;
    private float x_pos,y_pos,width,height,key_x,key_y,key_size;
    private String key;
    public ValueAnimator valueAnimator;
    private Paint paint = new Paint();
    public RectF rectF;
    public boolean isKeyPress = false;

    public KeyAndChar(Bitmap bitmap, float x_pos, float y_pos,float width, float height, String key, float key_size) {
        this.bitmap = bitmap;
        this.x_pos = GlobalVariables.xScale_factor*x_pos;
        this.y_pos = GlobalVariables.yScale_factor*y_pos;
        this.width = GlobalVariables.xScale_factor*width;
        this.height = GlobalVariables.yScale_factor*height;
        this.key_size = GlobalVariables.xScale_factor*key_size;
        paint.setTextSize(this.key_size);
        this.key_x = this.x_pos + (this.width - paint.measureText(key)) / 2;
        this.key_y = this.y_pos + this.height / 2 + this.height / 4;
        this.key = key;
        rectF = new RectF(this.x_pos, this.y_pos, this.x_pos + this.width, this.y_pos + this.height);
    }

    public void load(){
        PropertyValuesHolder x = PropertyValuesHolder.ofFloat("Y", key_y+ GlobalVariables.yScale_factor*20, key_y);
        PropertyValuesHolder size = PropertyValuesHolder.ofFloat("SIZE", key_size+key_size/2, key_size);

        valueAnimator = new ValueAnimator();
        valueAnimator.setValues(x,size);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                key_y = (float) animation.getAnimatedValue("Y");
                key_size = (float) animation.getAnimatedValue("SIZE");
            }
        });
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX_pos() {
        return x_pos;
    }

    public float getY_pos() {
        return y_pos;
    }

    public String getKey() {
        return key;
    }

    public float getKey_size() {
        return key_size;
    }

    public void clear(){
        isKeyPress = false;
        bitmap = Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.key_black);
    }

    public void drawKey(Canvas canvas , Paint paint){
        canvas.drawBitmap(bitmap,null,rectF,paint);
    }

    public void drawChar(Canvas canvas , Paint paint){
        if(valueAnimator != null){
            paint.setTextSize(key_size);
            canvas.drawText(key,key_x,key_y,paint);
        }
    }
}
