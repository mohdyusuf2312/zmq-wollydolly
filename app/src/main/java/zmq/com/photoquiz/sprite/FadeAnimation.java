package zmq.com.photoquiz.sprite;


import android.graphics.Paint;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;

public class FadeAnimation implements AnimationListener {

	public ShahSprite sprite;
	public Paint mCharacterPaint;
    private Transformation mTransformation;
    public AlphaAnimation mFadeIn;
    public boolean fadein;
	public Long duration;
	public FadeAnimation(ShahSprite sprite, Long duration, boolean fadein) {
		// TODO Auto-generated constructor stub
		this.sprite = sprite;
		this.fadein = fadein;
		mCharacterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTransformation = new Transformation();
		if (fadein) {
			mFadeIn = new AlphaAnimation(0f, 1f);
			mCharacterPaint.setAlpha(0);
		} else {
			mFadeIn = new AlphaAnimation(1f, 0f);
			mCharacterPaint.setAlpha(255);
		}
//		mCharacterPaint.setAlpha(255);
		this.duration=duration;
		mFadeIn.setDuration(duration);
		mFadeIn.setAnimationListener(this);
	}
	

	public void setFadein(boolean fadein) {
		this.fadein = fadein;
		mCharacterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTransformation = new Transformation();
		if (fadein) {
			mFadeIn = new AlphaAnimation(0f, 1f);
			mCharacterPaint.setAlpha(0);
		} else {
			mFadeIn = new AlphaAnimation(1f, 0f);
			mCharacterPaint.setAlpha(255);
		}
		mFadeIn.setDuration(duration);
		mFadeIn.setAnimationListener(this);
	}

	public void update() {
		if (mFadeIn.hasStarted() && !mFadeIn.hasEnded()) {
			mFadeIn.getTransformation(System.currentTimeMillis(),mTransformation);
			// Keep drawing until we are done
			mCharacterPaint.setAlpha((int) (255 * mTransformation.getAlpha()));
		}
	}
	public void start(){
		 mFadeIn.start();
		 mFadeIn.getTransformation(System.currentTimeMillis(), mTransformation);
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		System.out.println(" i  m in onAnimationStart");
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (fadein) {			
			mCharacterPaint.setAlpha(255);
		} else {			
			mCharacterPaint.setAlpha(0);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}
