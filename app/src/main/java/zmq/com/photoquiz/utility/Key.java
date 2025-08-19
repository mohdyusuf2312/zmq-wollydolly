package zmq.com.photoquiz.utility;

/**
 * Created by zmq181 on 20/3/19.
 */

public class Key {
    private boolean press;
    private String value;
    private float x_cord;
    private float y_cord;

    public boolean isPress() {
        return press;
    }

    public void setPress(boolean press) {
        this.press = press;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public float getX_cord() {
        return x_cord;
    }

    public void setX_cord(float x_cord) {
        this.x_cord = x_cord;
    }

    public float getY_cord() {
        return y_cord;
    }

    public void setY_cord(float y_cord) {
        this.y_cord = y_cord;
    }
}
