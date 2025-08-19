package zmq.com.photoquiz.canvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import zmq.com.photoquiz.R;
import zmq.com.photoquiz.activity.PlayActivity;
import zmq.com.photoquiz.utility.GlobalVariables;


public abstract class BaseSurface extends SurfaceView implements SurfaceHolder.Callback {

    public GameThread gameThread;
    public Context context;
    public Paint paint;
    public SurfaceHolder surfaceHolder;
    @SuppressLint("ResourceType")
    public BaseSurface(Context context) {
        super(context);
        this.context = context;
        paint = GlobalVariables.paint;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        this.setId(123);
    }

    public void setHolder() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    abstract protected void drawSomething(Canvas g);

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println(" BaseSurface surfaceCreated Called...");
        if (gameThread == null) {
            gameThread = new GameThread(surfaceHolder);
            gameThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread != null) {
            gameThread.running = false;
            gameThread.interrupt();
            gameThread = null;
        }
        surfaceHolder.removeCallback(this);
        System.out.println(" BaseSurface surfaceDestroyed Called...");
    }

    public class GameThread extends Thread {
        private SurfaceHolder _suHolder;
        public boolean running = true;

        public GameThread(SurfaceHolder surfaceHolder) {
            _suHolder = surfaceHolder;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = _suHolder.lockCanvas();
                    synchronized (_suHolder) {
                        drawSomething(canvas);
                    }
                    Thread.sleep(GlobalVariables.sleepTime);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (canvas != null)
                            _suHolder.unlockCanvasAndPost(canvas);
                        System.out.println("123");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}


