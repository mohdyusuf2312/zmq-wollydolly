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
        
        // Initialize paint if GlobalVariables.paint is null
        if (GlobalVariables.paint == null) {
            GlobalVariables.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        this.paint = GlobalVariables.paint;
        
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
            System.out.println(" BaseSurface: Creating new GameThread...");
            gameThread = new GameThread(surfaceHolder);
            gameThread.start();
            System.out.println(" BaseSurface: GameThread started successfully");
        } else {
            System.out.println(" BaseSurface: GameThread already exists");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println(" BaseSurface surfaceChanged Called... width: " + width + ", height: " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println(" BaseSurface surfaceDestroyed Called...");
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
            System.out.println(" BaseSurface GameThread: Starting render loop...");
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = _suHolder.lockCanvas();
                    if (canvas != null) {
                        synchronized (_suHolder) {
                            System.out.println(" BaseSurface GameThread: Calling drawSomething...");
                            drawSomething(canvas);
                        }
                    } else {
                        System.out.println(" BaseSurface GameThread: Failed to lock canvas");
                    }
                    Thread.sleep(GlobalVariables.sleepTime);
                } catch (Exception e) {
                    System.out.println(" BaseSurface GameThread: Error in render loop: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        if (canvas != null) {
                            _suHolder.unlockCanvasAndPost(canvas);
                            System.out.println(" BaseSurface GameThread: Frame rendered");
                        }
                    } catch (Exception e) {
                        System.out.println(" BaseSurface GameThread: Error posting canvas: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            System.out.println(" BaseSurface GameThread: Render loop ended");
        }
    }
}


