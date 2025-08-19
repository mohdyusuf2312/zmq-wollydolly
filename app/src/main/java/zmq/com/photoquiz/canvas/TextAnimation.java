package zmq.com.photoquiz.canvas;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import zmq.com.photoquiz.utility.GlobalVariables;

/**
 * Created by zmq181 on 10/4/19.
 */

enum Animation{
    SRINK,SPREAD
}

public class TextAnimation {
    private String text;
    private float x_pos;
    private float y_pos;
    private int color;
    private float text_size;
    private Typeface type_face;
    public ValueAnimator valueAnimator;

    private TextAnimation(Builder builder) {
        this.text = builder.text;
        this.x_pos = builder.x_pos;
        this.y_pos = builder.y_pos;
        this.color = builder.color;
        this.text_size = builder.text_size;
        this.type_face = builder.type_face;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setValueAnimator(Animation animator) {
        switch (animator){
            case SPREAD:
                PropertyValuesHolder x = PropertyValuesHolder.ofFloat("X", x_pos+GlobalVariables.xScale_factor*12, x_pos);
                PropertyValuesHolder size = PropertyValuesHolder.ofFloat("SIZE", 1, text_size);

                valueAnimator = new ValueAnimator();
                valueAnimator.setValues(x,size);
                valueAnimator.setDuration(600);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        x_pos = (float) animation.getAnimatedValue("X");
                        text_size = (float) animation.getAnimatedValue("SIZE");
                    }
                });
                break;

            case SRINK:
                break;
        }
    }

    public String getText() {
        return text;
    }

    public float getX_pos() {
        return x_pos;
    }

    public float getY_pos() {
        return y_pos;
    }

    public int getColor() {
        return color;
    }

    public float getText_size() {
        return text_size;
    }

    public Typeface getType_face() {
        return type_face;
    }

    public static class Builder {
        private String text;
        private float x_pos;
        private float y_pos;
        private int color;
        private float text_size;
        private Typeface type_face;

        public Builder(String text) {
            this.text = text;
            this.x_pos = 0;
            this.y_pos = 0;
            this.color = Color.YELLOW;
            this.text_size = GlobalVariables.xScale_factor * 18;

        }

        public Builder setPosition(float x_pos,float y_pos) {
            this.x_pos = GlobalVariables.xScale_factor*x_pos;
            this.y_pos = GlobalVariables.yScale_factor*y_pos;
            return this;
        }


        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setText_size(float text_size) {
            this.text_size = GlobalVariables.xScale_factor * text_size;
            return this;
        }

        public Builder setType_face(Typeface type_face) {
            this.type_face = type_face;
            return this;
        }

        public TextAnimation build() {
            return new TextAnimation(this);
        }
    }

    public void drawText(Canvas canvas, Paint paint) {
        paint.setColor(this.color);
        paint.setTextSize(this.text_size);
        if (type_face != null)
            paint.setTypeface(this.type_face);

        canvas.drawText(text,x_pos,y_pos,paint);
    }
}
