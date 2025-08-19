package zmq.com.photoquiz.canvas;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;

import zmq.com.photoquiz.utility.GlobalVariables;

/**
 * Created by zmq181 on 5/2/19.
 */

public class AnimationSpirit {

    private Bitmap bitmap;
    private float x_position, y_position, frame_width, frame_height, image_width, image_height;
    public static final int SCALE_IN = 1;
    public static final int SCALE_OUT_FROM = 2;
    public static final int SCALE_OUT_IN = 3;
    private int animation_type;
    public boolean anim = false;
    public ValueAnimator valueAnimator;

    private AnimationSpirit(Builder builder) {
        this.bitmap = builder.bitmap;
        this.x_position = builder.x_position;
        this.y_position = builder.y_position;
        this.frame_width = builder.frame_width;
        this.frame_height = builder.frame_height;
        this.image_width = builder.bitmap.getWidth();
        this.image_height = builder.bitmap.getHeight();
    }

    public void load(int animation) {
        animation_type = animation;
        switch (animation) {
            case SCALE_IN:
                PropertyValuesHolder x = PropertyValuesHolder.ofFloat("X", x_position, x_position+frame_width/2);
                PropertyValuesHolder y = PropertyValuesHolder.ofFloat("Y", y_position, y_position+frame_height/2);
                PropertyValuesHolder width = PropertyValuesHolder.ofFloat("WIDTH", frame_width, 0);
                PropertyValuesHolder height = PropertyValuesHolder.ofFloat("HEIGHT", frame_height, 0);

                valueAnimator = new ValueAnimator();
                valueAnimator.setValues(x, y,width,height);
                valueAnimator.setDuration(1000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        x_position = (float) animation.getAnimatedValue("X");
                        y_position = (float) animation.getAnimatedValue("Y");
                        frame_width = (float) animation.getAnimatedValue("WIDTH");
                        frame_height = (float) animation.getAnimatedValue("HEIGHT");
                        System.out.println(x_position+"  "+y_position+"  "+frame_width+"  "+frame_height);
                    }
                });
                break;

            case SCALE_OUT_IN:
                x = PropertyValuesHolder.ofFloat("X", x_position+frame_width/2, x_position);
                y = PropertyValuesHolder.ofFloat("Y", y_position+frame_height/2,y_position );
                width = PropertyValuesHolder.ofFloat("WIDTH", 0,frame_width);
                height = PropertyValuesHolder.ofFloat("HEIGHT", 0,frame_height);

                valueAnimator = new ValueAnimator();
                valueAnimator.setValues(x, y,width,height);
                valueAnimator.setDuration(1000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        x_position = (float) animation.getAnimatedValue("X");
                        y_position = (float) animation.getAnimatedValue("Y");
                        frame_width = (float) animation.getAnimatedValue("WIDTH");
                        frame_height = (float) animation.getAnimatedValue("HEIGHT");
                        System.out.println(x_position+"  "+y_position+"  "+frame_width+"  "+frame_height);
                    }
                });
                break;

            case SCALE_OUT_FROM:
                x = PropertyValuesHolder.ofFloat("X", x_position - frame_width / 2, x_position);
                y = PropertyValuesHolder.ofFloat("Y", y_position - frame_height / 2,y_position );
                width = PropertyValuesHolder.ofFloat("WIDTH", 2 * frame_width,frame_width);
                height = PropertyValuesHolder.ofFloat("HEIGHT", 2 * frame_height,frame_height);

                valueAnimator = new ValueAnimator();
                valueAnimator.setValues(x, y,width,height);
                valueAnimator.setDuration(1000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        x_position = (float) animation.getAnimatedValue("X");
                        y_position = (float) animation.getAnimatedValue("Y");
                        frame_width = (float) animation.getAnimatedValue("WIDTH");
                        frame_height = (float) animation.getAnimatedValue("HEIGHT");
                        System.out.println(x_position+"  "+y_position+"  "+frame_width+"  "+frame_height);
                    }
                });
                break;

            default:
                break;
        }
    }


    public float getWidth() {
        return image_width;
    }

    public float getHeight() {
        return image_height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getX_position() {
        return x_position;
    }

    public float getY_position() {
        return y_position;
    }

    public float getFrame_width() {
        return frame_width;
    }

    public float getFrame_height() {
        return frame_height;
    }


    public void paint(Canvas g, Paint paint) {
        if (g == null) {
            throw new NullPointerException("canvas is null");
        }
        switch (animation_type) {
            case SCALE_IN:
                g.drawBitmap(bitmap, null, new RectF(x_position, y_position, x_position + frame_width, y_position + getFrame_height()), paint);
                break;

            case SCALE_OUT_IN:
                g.drawBitmap(bitmap, null, new RectF(x_position, y_position, x_position + frame_width, y_position + getFrame_height()), paint);
                break;

            case SCALE_OUT_FROM:
                g.drawBitmap(bitmap, null, new RectF(x_position, y_position, x_position + frame_width, y_position + getFrame_height()), paint);
                break;

            default:
                g.drawBitmap(bitmap, null, new RectF(x_position, y_position, x_position + frame_width, y_position + frame_height), paint);
                break;
        }
    }


    public static class Builder {
        private Bitmap bitmap;
        private float x_position, y_position, frame_width, frame_height;

        public Builder(Bitmap bitmap) {
            this.bitmap = bitmap;
            frame_width = bitmap.getWidth();
            frame_height = bitmap.getHeight();
        }

        public Builder setPostion(float x_position, float y_position) {
            this.x_position = x_position*GlobalVariables.xScale_factor;
            this.y_position = y_position*GlobalVariables.yScale_factor;
            return this;
        }

        public Builder setFrame(float frame_width, float frame_height) {
            this.frame_width = frame_width*GlobalVariables.xScale_factor;
            this.frame_height = frame_height*GlobalVariables.yScale_factor;
            return this;
        }

        public AnimationSpirit build() {
            return new AnimationSpirit(this);
        }
    }

    public void recycle(){
        bitmap.recycle();
        System.gc();
    }
}
