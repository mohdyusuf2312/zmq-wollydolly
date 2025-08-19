package zmq.com.photoquiz.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import zmq.com.photoquiz.utility.Utility;


public class ShahSprite extends Layer {

	// ----- definitions for the various transformations possible -----

	/**
	 * No transform is applied to the ShahSprite. This constant has a value of
	 * <code>0</code>.
	 */
	public static final int TRANS_NONE = 0;

	/**
	 * Causes the ShahSprite to appear rotated clockwise by 90 degrees. This
	 * constant has a value of <code>5</code>.
	 */
	public static final int TRANS_ROT90 = 5;

	/**
	 * Causes the ShahSprite to appear rotated clockwise by 180 degrees. This
	 * constant has a value of <code>3</code>.
	 */
	public static final int TRANS_ROT180 = 3;

	/**
	 * Causes the ShahSprite to appear rotated clockwise by 270 degrees. This
	 * constant has a value of <code>6</code>.
	 */
	public static final int TRANS_ROT270 = 6;

	/**
	 * Causes the ShahSprite to appear reflected about its vertical center. This
	 * constant has a value of <code>2</code>.
	 */
	public static final int TRANS_MIRROR = 2;

	/**
	 * Causes the ShahSprite to appear reflected about its vertical center and then
	 * rotated clockwise by 90 degrees. This constant has a value of
	 * <code>7</code>.
	 */
	public static final int TRANS_MIRROR_ROT90 = 7;

	/**
	 * Causes the ShahSprite to appear reflected about its vertical center and then
	 * rotated clockwise by 180 degrees. This constant has a value of
	 * <code>1</code>.
	 */
	public static final int TRANS_MIRROR_ROT180 = 1;

	/**
	 * Causes the ShahSprite to appear reflected about its vertical center and then
	 * rotated clockwise by 270 degrees. This constant has a value of
	 * <code>4</code>.
	 */
	public static final int TRANS_MIRROR_ROT270 = 4;

	// ----- Constructors -----

	/**
	 * Creates a new non-animated ShahSprite using the provided Image. This
	 * constructor is functionally equivalent to calling
	 * <code>new ShahSprite(image, image.getWidth(), image.getHeight())</code>
	 * <p>
	 * By default, the ShahSprite is visible and its upper-left corner is positioned
	 * at (0,0) in the painter's coordinate system. <br>
	 * 
	 * @param image
	 *            the <code>Image</code> to use as the single frame for the
	 *            </code>ShahSprite
	 * @throws NullPointerException
	 *             if <code>img</code> is <code>null</code>
	 */
	private int z;
    private int sequence;
    private String characterId;

	public String getCharacterId() {
		return characterId;
	}

	public void setCharacterId(String characterId) {
		this.characterId = characterId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public ShahSprite(Bitmap image) {
		super(image.getWidth(), image.getHeight());

		initializeFrames(image, image.getWidth(), image.getHeight(), false);

		// initialize collision rectangle
		initCollisionRectBounds();

		// current transformation is TRANS_NONE
		this.setTransformImpl(TRANS_NONE);

	}

	/**
	 * Creates a new ShahSprite from another ShahSprite.
	 * <p>
	 *
	 * All instance attributes (raw frames, position, frame sequence, current
	 * frame, reference point, collision rectangle, transform, and visibility)
	 * of the source ShahSprite are duplicated in the new ShahSprite.
	 *
	 * @param s
	 *            the <code>ShahSprite</code> to create a copy of
	 * @throws NullPointerException
	 *             if <code>s</code> is <code>null</code>
	 *
	 */
	public ShahSprite(ShahSprite s) {

		super(s != null ? s.getWidth() : 0, s != null ? s.getHeight() : 0);

		if (s == null) {
			throw new NullPointerException();
		}

		// this.sourceImage = Bitmap.createImage(s.sourceImage);
		this.sourceImage = Bitmap.createBitmap(s.sourceImage);

		this.numberFrames = s.numberFrames;

		this.frameCoordsX = new int[this.numberFrames];
		this.frameCoordsY = new int[this.numberFrames];

		System.arraycopy(s.frameCoordsX, 0, this.frameCoordsX, 0,
				s.getRawFrameCount());

		System.arraycopy(s.frameCoordsY, 0, this.frameCoordsY, 0,
				s.getRawFrameCount());

		this.x = s.getX();
		this.y = s.getY();

		// these fields are set by defining a reference point
		this.dRefX = s.dRefX;
		this.dRefY = s.dRefY;

		// these fields are set when defining a collision rectangle
		this.collisionRectX = s.collisionRectX;
		this.collisionRectY = s.collisionRectY;
		this.collisionRectWidth = s.collisionRectWidth;
		this.collisionRectHeight = s.collisionRectHeight;

		// these fields are set when creating a ShahSprite from an Image
		this.srcFrameWidth = s.srcFrameWidth;
		this.srcFrameHeight = s.srcFrameHeight;

		// the above fields are used in setTransform()
		// which is why we set them first, then call setTransformImpl()
		// to set up internally used data structures.
		setTransformImpl(s.t_currentTransformation);

		this.setVisible(s.isVisible());

		this.frameSequence = new int[s.getFrameSequenceLength()];
		this.setFrameSequence(s.frameSequence);
		this.setFrame(s.getFrame());

		this.setRefPixelPosition(s.getRefPixelX(), s.getRefPixelY());

	}

	/*
	 * creat a new sprite from a sprite of desired frame size
	 * */
	public ShahSprite(ShahSprite s, int frameWidth, int frameHeight) {
		this(s.sourceImage,frameWidth,frameHeight);
	}

    // ----- public methods -----
	/**
	 * Defines the reference pixel for this ShahSprite. The pixel is defined by its
	 * location relative to the upper-left corner of the ShahSprite's un-transformed
	 * frame, and it may lay outside of the frame's bounds.
	 * <p>
	 * When a transformation is applied, the reference pixel is defined relative
	 * to the ShahSprite's initial upper-left corner before transformation. This
	 * corner may nobtn longer appear as the upper-left corner in the painter's
	 * coordinate system under current transformation.
	 * <p>
	 * By default, a ShahSprite's reference pixel is located at (0,0); that is, the
	 * pixel in the upper-left corner of the raw frame.
	 * <p>
	 * Changing the reference pixel does not change the ShahSprite's physical
	 * position in the painter's coordinate system; that is, the values returned
	 * by {@link #getX getX()} and {@link #getY getY()} will not change as a
	 * result of defining the reference pixel. However, subsequent calls to
	 * methods that involve the reference pixel will be impacted by its new
	 * definition.
	 *
	 * @param x
	 *            the horizontal location of the reference pixel, relative to
	 *            the left edge of the un-transformed frame
	 * @param y
	 *            the vertical location of the reference pixel, relative to the
	 *            top edge of the un-transformed frame
	 * @see #setRefPixelPosition
	 * @see #getRefPixelX
	 * @see #getRefPixelY
	 */
	public void defineReferencePixel(int x, int y) {
		dRefX = x;
		dRefY = y;
	}

	/**
	 * Sets this ShahSprite's position such that its reference pixel is located at
	 * (x,y) in the painter's coordinate system.
	 *
	 * @param x
	 *            the horizontal location at which to place the reference pixel
	 * @param y
	 *            the vertical location at which to place the reference pixel
	 * @see #defineReferencePixel
	 * @see #getRefPixelX
	 * @see #getRefPixelY
	 */
	public void setRefPixelPosition(int x, int y) {

		// update this.x and this.y
		this.x = x
				- getTransformedPtX(dRefX, dRefY, this.t_currentTransformation);
		this.y = y
				- getTransformedPtY(dRefX, dRefY, this.t_currentTransformation);

	}

	/**
	 * Gets the horizontal position of this ShahSprite's reference pixel in the
	 * painter's coordinate system.
	 *
	 * @return the horizontal location of the reference pixel
	 * @see #defineReferencePixel
	 * @see #setRefPixelPosition
	 * @see #getRefPixelY
	 */
	public int getRefPixelX() {
		return (this.x + getTransformedPtX(dRefX, dRefY,
				this.t_currentTransformation));
	}

	/**
	 * Gets the vertical position of this ShahSprite's reference pixel in the
	 * painter's coordinate system.
	 *
	 * @return the vertical location of the reference pixel
	 * @see #defineReferencePixel
	 * @see #setRefPixelPosition
	 * @see #getRefPixelX
	 */
	public int getRefPixelY() {
		return (this.y + getTransformedPtY(dRefX, dRefY,
				this.t_currentTransformation));
	}

	/**
	 * Selects the current frame in the frame sequence.
	 * <p>
	 * The current frame is rendered when {@link #paint(Graphics)} is called.
	 * <p>
	 * The index provided refers to the desired entry in the frame sequence, not
	 * the index of the actual frame itself.
	 *
	 * @param sequenceIndex
	 *            the index of of the desired entry in the frame sequence
	 * @throws IndexOutOfBoundsException
	 *             if <code>frameIndex</code> is less than<code>0</code>
	 * @throws IndexOutOfBoundsException
	 *             if <code>frameIndex</code> is equal to or greater than the
	 *             length of the current frame sequence (or the number of raw
	 *             frames for the default sequence)
	 * @see #setFrameSequence(int[])
	 * @see #getFrame
	 */
	public void setFrame(int sequenceIndex) {
		if (sequenceIndex < 0 || sequenceIndex >= frameSequence.length) {
			throw new IndexOutOfBoundsException();
		}
		this.sequenceIndex = sequenceIndex;
	}

	/**
	 * Gets the current index in the frame sequence.
	 * <p>
	 * The index returned refers to the current entry in the frame sequence, not
	 * the index of the actual frame that is displayed.
	 *
	 * @return the current index in the frame sequence
	 * @see #setFrameSequence(int[])
	 * @see #setFrame
	 */
	public final int getFrame() {
		return sequenceIndex;
	}

	/**
	 * Gets the number of raw frames for this ShahSprite. The value returned
	 * reflects the number of frames; it does not reflect the length of the
	 * ShahSprite's frame sequence. However, these two values will be the same if
	 * the default frame sequence is used.
	 *
	 * @return the number of raw frames for this ShahSprite
	 * @see #getFrameSequenceLength
	 */
	public int getRawFrameCount() {
		return numberFrames;
	}

	/**
	 * Gets the number of elements in the frame sequence. The value returned
	 * reflects the length of the ShahSprite's frame sequence; it does not reflect
	 * the number of raw frames. However, these two values will be the same if
	 * the default frame sequence is used.
	 *
	 * @return the number of elements in this ShahSprite's frame sequence
	 * @see #getRawFrameCount
	 */
	public int getFrameSequenceLength() {
		return frameSequence.length;
	}

	/**
	 * Selects the next frame in the frame sequence.
	 * <p>
	 *
	 * The frame sequence is considered to be circular, i.e. if
	 * {@link #nextFrame} is called when at the end of the sequence, this method
	 * will advance to the first entry in the sequence.
	 *
	 * @see #setFrameSequence(int[])
	 * @see #prevFrame
	 */
	public void nextFrame() {
		sequenceIndex = (sequenceIndex + 1) % frameSequence.length;
	}

	/**
	 * Selects the previous frame in the frame sequence.
	 * <p>
	 *
	 * The frame sequence is considered to be circular, i.e. if
	 * {@link #prevFrame} is called when at the start of the sequence, this
	 * method will advance to the last entry in the sequence.
	 *
	 * @see #setFrameSequence(int[])
	 * @see #nextFrame
	 */
	public void prevFrame() {
		if (sequenceIndex == 0) {
			sequenceIndex = frameSequence.length - 1;
		} else {
			sequenceIndex--;
		}
	}

	/**
	 * Draws the ShahSprite.
	 * <P>
	 * Draws current frame of ShahSprite using the provided Graphics object. The
	 * ShahSprite's upper left corner is rendered at the ShahSprite's current position
	 * relative to the origin of the Graphics object. The current position of
	 * the ShahSprite's upper-left corner can be retrieved by calling
	 * {@link #getX()} and {@link #getY()}.
	 * <P>
	 * Rendering is subject to the clip region of the Graphics object. The
	 * ShahSprite will be drawn only if it is visible.
	 * <p>
	 * If the ShahSprite's Image is mutable, the ShahSprite is rendered using the
	 * current contents of the Image.
	 *
	 * @param g
	 *            the graphics object to draw <code>ShahSprite</code> on
	 * @throws NullPointerException
	 *             if <code>g</code> is <code>null</code>
	 *
	 */
	public final void paint(Canvas g, Paint paint) {// with scalling
		// managing the painting order is the responsibility of
		// the layermanager, so depth is ignored


		if (g == null) {
			System.out.println("Got Exception...");
			throw new NullPointerException();
		}

		if (visible) {

			// width and height of the source
			// image is the width and height
			// of the original frame
			// g.drawRegion(sourceImage,
			// frameCoordsX[frameSequence[sequenceIndex]],
			// frameCoordsY[frameSequence[sequenceIndex]],
			// srcFrameWidth,
			// srcFrameHeight,
			// t_currentTransformation,
			// this.x,
			// this.y,
			// Graphics.TOP | Graphics.LEFT);
//			x=(int)(x*GlobalVariables.xScale_factor);
//			y=(int)(y*GlobalVariables.yScale_factor);
//			if(GlobalVariables.xScale_factor!=0&&GlobalVariables.yScale_factor!=0){
//			g.drawBitmap(sourceImage, new Rect(
//					frameCoordsX[frameSequence[sequenceIndex]],
//					frameCoordsY[frameSequence[sequenceIndex]], frameCoordsX[frameSequence[sequenceIndex]]+srcFrameWidth,
//					frameCoordsY[frameSequence[sequenceIndex]]+srcFrameHeight), new Rect(x, y, x + (int)(srcFrameWidth*GlobalVariables.xScale_factor), y
//					+ (int)(srcFrameHeight*GlobalVariables.yScale_factor)), paint);
//			}else{
//				g.drawBitmap(sourceImage, new Rect(
//						frameCoordsX[frameSequence[sequenceIndex]],
//						frameCoordsY[frameSequence[sequenceIndex]], frameCoordsX[frameSequence[sequenceIndex]]+srcFrameWidth,
//						frameCoordsY[frameSequence[sequenceIndex]]+srcFrameHeight), new Rect(x, y, x + srcFrameWidth, y
//						+ srcFrameHeight), paint);
//				}
            g.drawBitmap(sourceImage, new Rect(
                    frameCoordsX[frameSequence[sequenceIndex]],
                    frameCoordsY[frameSequence[sequenceIndex]],
					frameCoordsX[frameSequence[sequenceIndex]]+srcFrameWidth,
                    frameCoordsY[frameSequence[sequenceIndex]]+srcFrameHeight),
					new Rect(x, y, x + srcFrameWidth, y
                    + srcFrameHeight), paint);

			}
		}

	/**
	 * Set the frame sequence for this ShahSprite.
	 * <p>
	 *
	 * All Sprites have a default sequence that displays the Sprites frames in
	 * order. This method allows for the creation of an arbitrary sequence using
	 * the available frames. The current index in the frame sequence is reset to
	 * zero as a result of calling this method.
	 * <p>
	 * The contents of the sequence array are copied when this method is called;
	 * thus, any changes made to the array after this method returns have nobtn
	 * effect on the ShahSprite's frame sequence.
	 * <P>
	 * Passing in <code>null</code> causes the ShahSprite to revert to the default
	 * frame sequence.
	 * <p>
	 *
	 * @param sequence
	 *            an array of integers, where each integer represents a frame
	 *            index
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 *             if seq is non-null and any member of the array has a value
	 *             less than <code>0</code> or greater than or equal to the
	 *             number of frames as reported by {@link #getRawFrameCount()}
	 * @throws IllegalArgumentException
	 *             if the array has less than <code>1</code> element
	 * @see #nextFrame
	 * @see #prevFrame
	 * @see #setFrame
	 * @see #getFrame
	 *
	 */
	public void setFrameSequence(int sequence[]) {

		if (sequence == null) {
			// revert to the default sequence
			sequenceIndex = 0;
			customSequenceDefined = false;
			frameSequence = new int[numberFrames];
			// copy frames indices into frameSequence
			for (int i = 0; i < numberFrames; i++) {
				frameSequence[i] = i;
			}
			return;
		}

		if (sequence.length < 1) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < sequence.length; i++) {
			if (sequence[i] < 0 || sequence[i] >= numberFrames) {
				throw new ArrayIndexOutOfBoundsException();
			}
		}
		customSequenceDefined = true;
		frameSequence = new int[sequence.length];
		System.arraycopy(sequence, 0, frameSequence, 0, sequence.length);
		sequenceIndex = 0;
	}


	/**
	 * Changes the Image containing the ShahSprite's frames.
	 * <p>
	 * Replaces the current raw frames of the ShahSprite with a new set of raw
	 * frames. See the constructor {@link #ShahSprite(android.media.Image, int, int)} for
	 * information on how the frames are created from the image. The values
	 * returned by {@link Layer#getWidth} and {@link Layer#getHeight} will
	 * reflect the new frame width and frame height subject to the ShahSprite's
	 * current transform.
	 * <p>
	 * Changing the image for the ShahSprite could change the number of raw frames.
	 * If the new frame set has as many or more raw frames than the previous
	 * frame set, then:
	 * <ul>
	 * <li>The current frame will be unchanged
	 * <li>If a custom frame sequence has been defined (using
	 * {@link #setFrameSequence(int[])}), it will remain unchanged. If nobtn custom
	 * frame sequence is defined (i.e. the default frame sequence is in use),
	 * the default frame sequence will be updated to be the default frame
	 * sequence for the new frame set. In other words, the new default frame
	 * sequence will include all of the frames from the new raw frame set, as if
	 * this new image had been used in the constructor.
	 * </ul>
	 * <p>
	 * If the new frame set has fewer frames than the previous frame set, then:
	 * <ul>
	 * <li>The current frame will be reset to entry 0
	 * <li>Any custom frame sequence will be discarded and the frame sequence
	 * will revert to the default frame sequence for the new frame set.
	 * </ul>
	 * <p>
	 * The reference point location is unchanged as a result of calling this
	 * method, both in terms of its defined location within the ShahSprite and its
	 * position in the painter's coordinate system. However, if the frame size
	 * is changed and the ShahSprite has been transformed, the position of the
	 * ShahSprite's upper-left corner may change such that the reference point
	 * remains stationary.
	 * <p>
	 * If the ShahSprite's frame size is changed by this method, the collision
	 * rectangle is reset to its default value (i.e. it is set to the new bounds
	 * of the untransformed ShahSprite).
	 * <p>
	 *
	 * @param img
	 *            the <code>Image</code> to use for <code>ShahSprite</code>
	 * @param frameWidth
	 *            the width in pixels of the individual raw frames
	 * @param frameHeight
	 *            the height in pixels of the individual raw frames
	 * @throws NullPointerException
	 *             if <code>img</code> is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if <code>frameHeight</code> or <code>frameWidth</code> is
	 *             less than <code>1</code>
	 * @throws IllegalArgumentException
	 *             if the image width is not an integer multiple of the
	 *             <code>frameWidth</code>
	 * @throws IllegalArgumentException
	 *             if the image height is not an integer multiple of the
	 *             <code>frameHeight</code>
	 */
	public void setImage(Bitmap img, int frameWidth, int frameHeight) {
         System.out.println(img.getWidth()+"  "+getHeight());

        if (img.getWidth()%frameWidth!=0) {
            img= Utility.getResizedBitmap(img, img.getWidth() - img.getWidth() % frameWidth, frameHeight);
        }
		// if image is null image.getWidth() will throw NullPointerException
		if ((frameWidth < 1 || frameHeight < 1)
				|| ((img.getWidth() % frameWidth) != 0)
				|| ((img.getHeight() % frameHeight) != 0)) {
			throw new IllegalArgumentException();
		}

		int noOfFrames = (img.getWidth() / frameWidth)
				* (img.getHeight() / frameHeight);

		boolean maintainCurFrame = true;
		if (noOfFrames < numberFrames) {
			// use default frame , sequence index = 0
			maintainCurFrame = false;
			customSequenceDefined = false;
		}

		if (!((srcFrameWidth == frameWidth) && (srcFrameHeight == frameHeight))) {

			// computing is the location
			// of the reference pixel in the painter's coordinate system.
			// and then use this to find x and y position of the ShahSprite
			int oldX = this.x
					+ getTransformedPtX(dRefX, dRefY,
							this.t_currentTransformation);

			int oldY = this.y
					+ getTransformedPtY(dRefX, dRefY,
							this.t_currentTransformation);

			setWidthImpl(frameWidth);
			setHeightImpl(frameHeight);

			initializeFrames(img, frameWidth, frameHeight, maintainCurFrame);

			// initialize collision rectangle
			initCollisionRectBounds();

			// set the new x and y position of the ShahSprite
			this.x = oldX
					- getTransformedPtX(dRefX, dRefY,
							this.t_currentTransformation);

			this.y = oldY
					- getTransformedPtY(dRefX, dRefY,
							this.t_currentTransformation);

			// Calculate transformed sprites collision rectangle
			// and transformed width and height

			computeTransformedBounds(this.t_currentTransformation);

		} else {
			// just reinitialize the animation frames.
			initializeFrames(img, frameWidth, frameHeight, maintainCurFrame);
		}

	}

	/**
	 * Defines the ShahSprite's bounding rectangle that is used for collision
	 * detection purposes. This rectangle is specified relative to the
	 * un-transformed ShahSprite's upper-left corner and defines the area that is
	 * checked for collision detection. For pixel-level detection, only those
	 * pixels within the collision rectangle are checked.
	 *
	 * By default, a ShahSprite's collision rectangle is located at 0,0 as has the
	 * same dimensions as the ShahSprite. The collision rectangle may be specified
	 * to be larger or smaller than the default rectangle; if made larger, the
	 * pixels outside the bounds of the ShahSprite are considered to be transparent
	 * for pixel-level collision detection.
	 *
	 * @param x
	 *            the horizontal location of the collision rectangle relative to
	 *            the untransformed ShahSprite's left edge
	 * @param y
	 *            the vertical location of the collision rectangle relative to
	 *            the untransformed ShahSprite's top edge
	 * @param width
	 *            the width of the collision rectangle
	 * @param height
	 *            the height of the collision rectangle
	 * @throws IllegalArgumentException
	 *             if the specified <code>width</code> or <code>height</code> is
	 *             less than <code>0</code>
	 */
	public void defineCollisionRectangle(int x, int y, int width, int height) {

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}

		collisionRectX = x;
		collisionRectY = y;
		collisionRectWidth = width;
		collisionRectHeight = height;

		// call set transform with current transformation to
		// update transformed sprites collision rectangle
		setTransformImpl(t_currentTransformation);
	}

	/**
	 * Sets the transform for this ShahSprite. Transforms can be applied to a ShahSprite
	 * to change its rendered appearance. Transforms are applied to the original
	 * ShahSprite image; they are not cumulative, nor can they be combined. By
	 * default, a ShahSprite's transform is {@link #TRANS_NONE}.
	 * <P>
	 * Since some transforms involve rotations of 90 or 270 degrees, their use
	 * may result in the overall width and height of the ShahSprite being swapped.
	 * As a result, the values returned by {@link Layer#getWidth} and
	 * {@link Layer#getHeight} may change.
	 * <p>
	 * The collision rectangle is also modified by the transform so that it
	 * remains static relative to the pixel data of the ShahSprite. Similarly, the
	 * defined reference pixel is unchanged by this method, but its visual
	 * location within the ShahSprite may change as a result.
	 * <P>
	 * This method repositions the ShahSprite so that the location of the reference
	 * pixel in the painter's coordinate system does not change as a result of
	 * changing the transform. Thus, the reference pixel effectively becomes the
	 * centerpoint for the transform. Consequently, the values returned by
	 * {@link #getRefPixelX} and {@link #getRefPixelY} will be the same both
	 * before and after the transform is applied, but the values returned by
	 * {@link #getX getX()} and {@link #getY getY()} may change.
	 * <p>
	 *
	 * @param transform
	 *            the desired transform for this <code>ShahSprite</code>
	 * @throws IllegalArgumentException
	 *             if the requested <code>transform</code> is invalid
	 * @see #TRANS_NONE
	 * @see #TRANS_ROT90
	 * @see #TRANS_ROT180
	 * @see #TRANS_ROT270
	 * @see #TRANS_MIRROR
	 * @see #TRANS_MIRROR_ROT90
	 * @see #TRANS_MIRROR_ROT180
	 * @see #TRANS_MIRROR_ROT270
	 *
	 */
	public void setTransform(int transform) {
		setTransformImpl(transform);
	}

	/**
	 * Checks for a collision between this ShahSprite and the specified ShahSprite.
	 * <P>
	 * If pixel-level detection is used, a collision is detected only if opaque
	 * pixels collide. That is, an opaque pixel in the first ShahSprite would have
	 * to collide with an opaque pixel in the second ShahSprite for a collision to
	 * be detected. Only those pixels within the Sprites' respective collision
	 * rectangles are checked.
	 * <P>
	 * If pixel-level detection is not used, this method simply checks if the
	 * Sprites' collision rectangles intersect.
	 * <P>
	 * Any transforms applied to the Sprites are automatically accounted for.
	 * <P>
	 * Both Sprites must be visible in order for a collision to be detected.
	 * <P>
	 *
	 * @param s
	 *            the <code>ShahSprite</code> to test for collision with
	 * @param pixelLevel
	 *            <code>true</code> to test for collision on a pixel-by-pixel
	 *            basis, <code>false</code> to test using simple bounds
	 *            checking.
	 * @return <code>true</code> if the two Sprites have collided, otherwise
	 *         <code>false</code>
	 * @throws NullPointerException
	 *             if ShahSprite <code>s</code> is <code>null</code>
	 */
	public final boolean collidesWith(ShahSprite s, boolean pixelLevel) {

		// check if either of the ShahSprite's are not visible
//		System.out.println("Collision:"+s.visible+"-"+this.visible);
		if(s==null){
			return false;
		}
		if (!(s.visible && this.visible)) {
			return false;
		}

		// these are package private
		// and can be accessed directly
		int otherLeft = s.x + s.t_collisionRectX;
		int otherTop = s.y + s.t_collisionRectY;
		int otherRight = otherLeft + s.t_collisionRectWidth;
		int otherBottom = otherTop + s.t_collisionRectHeight;

		int left = this.x + this.t_collisionRectX;
		int top = this.y + this.t_collisionRectY;
		int right = left + this.t_collisionRectWidth;
		int bottom = top + this.t_collisionRectHeight;

		// check if the collision rectangles of the two sprites intersect
		if (intersectRect(otherLeft, otherTop, otherRight, otherBottom, left,
				top, right, bottom)) {

			// collision rectangles intersect
			if (pixelLevel) {

				// we need to check pixel level collision detection.
				// use only the coordinates within the ShahSprite frame if
				// the collision rectangle is larger than the ShahSprite
				// frame
				if (this.t_collisionRectX < 0) {
					left = this.x;
				}
				if (this.t_collisionRectY < 0) {
					top = this.y;
				}
				if ((this.t_collisionRectX + this.t_collisionRectWidth) > this.width) {
					right = this.x + this.width;
				}
				if ((this.t_collisionRectY + this.t_collisionRectHeight) > this.height) {
					bottom = this.y + this.height;
				}

				// similarly for the other ShahSprite
				if (s.t_collisionRectX < 0) {
					otherLeft = s.x;
				}
				if (s.t_collisionRectY < 0) {
					otherTop = s.y;
				}
				if ((s.t_collisionRectX + s.t_collisionRectWidth) > s.width) {
					otherRight = s.x + s.width;
				}
				if ((s.t_collisionRectY + s.t_collisionRectHeight) > s.height) {
					otherBottom = s.y + s.height;
				}

				// recheck if the updated collision area rectangles intersect
				if (!intersectRect(otherLeft, otherTop, otherRight,
						otherBottom, left, top, right, bottom)) {

					// if they don't intersect, return false;
					return false;
				}

				// the updated collision rectangles intersect,
				// go ahead with collision detection

				// find intersecting region,
				// within the collision rectangles
				int intersectLeft = (left < otherLeft) ? otherLeft : left;
				int intersectTop = (top < otherTop) ? otherTop : top;

				// used once, optimize.
				int intersectRight = (right < otherRight) ? right : otherRight;
				int intersectBottom = (bottom < otherBottom) ? bottom
						: otherBottom;

				int intersectWidth = Math.abs(intersectRight - intersectLeft);
				int intersectHeight = Math.abs(intersectBottom - intersectTop);

				// have the coordinates in painter space,
				// need coordinates of top left and width, height
				// in source image of ShahSprite.

				int thisImageXOffset = getImageTopLeftX(intersectLeft,
						intersectTop, intersectRight, intersectBottom);

				int thisImageYOffset = getImageTopLeftY(intersectLeft,
						intersectTop, intersectRight, intersectBottom);

				int otherImageXOffset = s.getImageTopLeftX(intersectLeft,
						intersectTop, intersectRight, intersectBottom);

				int otherImageYOffset = s.getImageTopLeftY(intersectLeft,
						intersectTop, intersectRight, intersectBottom);

				// check if opaque pixels intersect.

//				return doPixelCollision(thisImageXOffset, thisImageYOffset,
//						otherImageXOffset, otherImageYOffset, this.sourceImage,
//						this.t_currentTransformation, s.sourceImage,
//						s.t_currentTransformation, intersectWidth,
//						intersectHeight);

			} else {
				// collides!
				return true;
			}
		}
		return false;

	}

	/**
	 * Checks for a collision between this ShahSprite and the specified Image with
	 * its upper left corner at the specified location. If pixel-level detection
	 * is used, a collision is detected only if opaque pixels collide. That is,
	 * an opaque pixel in the ShahSprite would have to collide with an opaque pixel
	 * in Image for a collision to be detected. Only those pixels within the
	 * ShahSprite's collision rectangle are checked.
	 * <P>
	 * If pixel-level detection is not used, this method simply checks if the
	 * ShahSprite's collision rectangle intersects with the Image's bounds.
	 * <P>
	 * Any transform applied to the ShahSprite is automatically accounted for.
	 * <P>
	 * The ShahSprite must be visible in order for a collision to be detected.
	 * <P>
	 *
	 * @param image
	 *            the <code>Image</code> to test for collision
	 * @param x
	 *            the horizontal location of the <code>Image</code>'s upper left
	 *            corner
	 * @param y
	 *            the vertical location of the <code>Image</code>'s upper left
	 *            corner
	 * @param pixelLevel
	 *            <code>true</code> to test for collision on a pixel-by-pixel
	 *            basis, <code>false</code> to test using simple bounds checking
	 * @return <code>true</code> if this <code>ShahSprite</code> has collided with
	 *         the <code>Image</code>, otherwise <code>false</code>
	 * @throws NullPointerException
	 *             if <code>image</code> is <code>null</code>
	 */
	public final boolean collidesWith(Bitmap image, int x, int y,
                                      boolean pixelLevel) {

		// check if this ShahSprite is not visible
		if (!(this.visible)) {
			return false;
		}

		// if image is null
		// image.getWidth() will throw NullPointerException
		int otherLeft = x;
		int otherTop = y;
		int otherRight = x + image.getWidth();
		int otherBottom = y + image.getHeight();

		int left = this.x + this.t_collisionRectX;
		int top = this.y + this.t_collisionRectY;
		int right = left + this.t_collisionRectWidth;
		int bottom = top + this.t_collisionRectHeight;

		// first check if the collision rectangles of the two sprites intersect
		if (intersectRect(otherLeft, otherTop, otherRight, otherBottom, left,
				top, right, bottom)) {

			// collision rectangles intersect
			if (pixelLevel) {

				// find intersecting region,

				// we need to check pixel level collision detection.
				// use only the coordinates within the ShahSprite frame if
				// the collision rectangle is larger than the ShahSprite
				// frame
				if (this.t_collisionRectX < 0) {
					left = this.x;
				}
				if (this.t_collisionRectY < 0) {
					top = this.y;
				}
				if ((this.t_collisionRectX + this.t_collisionRectWidth) > this.width) {
					right = this.x + this.width;
				}
				if ((this.t_collisionRectY + this.t_collisionRectHeight) > this.height) {
					bottom = this.y + this.height;
				}

				// recheck if the updated collision area rectangles intersect
				if (!intersectRect(otherLeft, otherTop, otherRight,
						otherBottom, left, top, right, bottom)) {

					// if they don't intersect, return false;
					return false;
				}

				// within the collision rectangles
				int intersectLeft = (left < otherLeft) ? otherLeft : left;
				int intersectTop = (top < otherTop) ? otherTop : top;

				// used once, optimize.
				int intersectRight = (right < otherRight) ? right : otherRight;
				int intersectBottom = (bottom < otherBottom) ? bottom
						: otherBottom;

				int intersectWidth = Math.abs(intersectRight - intersectLeft);
				int intersectHeight = Math.abs(intersectBottom - intersectTop);

				// have the coordinates in painter space,
				// need coordinates of top left and width, height
				// in source image of ShahSprite.

				int thisImageXOffset = getImageTopLeftX(intersectLeft,
						intersectTop, intersectRight, intersectBottom);

				int thisImageYOffset = getImageTopLeftY(intersectLeft,
						intersectTop, intersectRight, intersectBottom);

				int otherImageXOffset = intersectLeft - x;
				int otherImageYOffset = intersectTop - y;

				// check if opaque pixels intersect.
//				return doPixelCollision(thisImageXOffset, thisImageYOffset,
//						otherImageXOffset, otherImageYOffset, this.sourceImage,
//						this.t_currentTransformation, image, ShahSprite.TRANS_NONE,
//						intersectWidth, intersectHeight);

			} else {
				// collides!
				return true;
			}
		}
		return false;

	}

    /**
     * Creates a new animated ShahSprite using frames contained in the provided
     * Image. The frames must be equally sized, with the dimensions specified by
     * <code>frameWidth</code> and <code>frameHeight</code>. They may be laid
     * out in the image horizontally, vertically, or as a grid. The width of the
     * source image must be an integer multiple of the frame width, and the
     * height of the source image must be an integer multiple of the frame
     * height. The values returned by {@link Layer#getWidth} and
     * {@link Layer#getHeight} will reflect the frame width and frame height
     * subject to the ShahSprite's current transform.
     * <p>
     * Sprites have a default frame sequence corresponding to the raw frame
     * numbers, starting with frame 0. The frame sequence may be modified with
     * {@link #setFrameSequence(int[])}.
     * <p>
     * By default, the ShahSprite is visible and its upper-left corner is positioned
     * at (0,0) in the painter's coordinate system.
     * <p>
     *
     * @param image
     *            the <code>Image</code> to use for <code>ShahSprite</code>
     * @param frameWidth
     *            the <code>width</code>, in pixels, of the individual raw
     *            frames
     * @param frameHeight
     *            the <code>height</code>, in pixels, of the individual raw
     *            frames
     * @throws NullPointerException
     *             if <code>img</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>frameHeight</code> or <code>frameWidth</code> is
     *             less than <code>1</code>
     * @throws IllegalArgumentException
     *             if the <code>image</code> width is not an integer multiple of
     *             the <code>frameWidth</code>
     * @throws IllegalArgumentException
     *             if the <code>image</code> height is not an integer multiple
     *             of the <code>frameHeight</code>
     */
    public ShahSprite(Bitmap image, int frameWidth, int frameHeight) {

        super(frameWidth, frameHeight);
        // if img is null img.getWidth() will throw NullPointerException
        if(!(frameWidth < 1 || frameHeight < 1)&&image.getWidth()%frameWidth!=0){
            image= Utility.getResizedBitmap(image, image.getWidth() - image.getWidth() % frameWidth, frameHeight);

        }

        if ((frameWidth < 1 || frameHeight < 1)
                || ((image.getWidth() % frameWidth) != 0)
                || ((image.getHeight() % frameHeight) != 0)) {
            throw new IllegalArgumentException();
        }

        // construct the array of images that
        // we use as "frames" for the sprite.
        // use default frame , sequence index = 0
        initializeFrames(image, frameWidth, frameHeight, false);

        // initialize collision rectangle
        initCollisionRectBounds();

        // current transformation is TRANS_NONE
        this.setTransformImpl(TRANS_NONE);

    }

	// -----

	// ----- private -----

	/**
	 * create the Image Array.
	 * 
	 * @param image
	 *            Image to use for ShahSprite
	 * @param fWidth
	 *            width, in pixels, of the individual raw frames
	 * @param fHeight
	 *            height, in pixels, of the individual raw frames
	 * @param maintainCurFrame
	 *            true if Current Frame is maintained
	 */
//	public ArrayList<Rect>rectArrayList=new ArrayList<>();
	private void initializeFrames(Bitmap image, int fWidth, int fHeight,
                                  boolean maintainCurFrame) {

		int imageW = image.getWidth();
		int imageH = image.getHeight();

		int numHorizontalFrames = imageW / fWidth;
		int numVerticalFrames = imageH / fHeight;

		sourceImage = image;

		srcFrameWidth = fWidth;
		srcFrameHeight = fHeight;

		numberFrames = numHorizontalFrames * numVerticalFrames;

		frameCoordsX = new int[numberFrames];
		frameCoordsY = new int[numberFrames];

		if (!maintainCurFrame) {
			sequenceIndex = 0;
		}

		if (!customSequenceDefined) {
			frameSequence = new int[numberFrames];
		}

		int currentFrame = 0;

		for (int yy = 0; yy < imageH; yy += fHeight) {
			for (int xx = 0; xx < imageW; xx += fWidth) {

				frameCoordsX[currentFrame] = xx;
				frameCoordsY[currentFrame] = yy;

				if (!customSequenceDefined) {
					frameSequence[currentFrame] = currentFrame;
				}
//				rectArrayList.add(new Rect(frameCoordsX[frameSequence[currentFrame]],
//						frameCoordsY[frameSequence[currentFrame]],
//						frameCoordsX[frameSequence[currentFrame]]+srcFrameWidth,
//						frameCoordsY[frameSequence[currentFrame]]+srcFrameHeight));
				currentFrame++;

			}
		}
	}

	/**
	 * initialize the collision rectangle
	 */
	private void initCollisionRectBounds() {

		// reset x and y of collision rectangle
		collisionRectX = 0;
		collisionRectY = 0;

		// intialize the collision rectangle bounds to that of the sprite
		collisionRectWidth = this.width;
		collisionRectHeight = this.height;

	}

	/**
	 * Detect rectangle intersection
	 * 
	 * @param r1x1
	 *            left co-ordinate of first rectangle
	 * @param r1y1
	 *            top co-ordinate of first rectangle
	 * @param r1x2
	 *            right co-ordinate of first rectangle
	 * @param r1y2
	 *            bottom co-ordinate of first rectangle
	 * @param r2x1
	 *            left co-ordinate of second rectangle
	 * @param r2y1
	 *            top co-ordinate of second rectangle
	 * @param r2x2
	 *            right co-ordinate of second rectangle
	 * @param r2y2
	 *            bottom co-ordinate of second rectangle
	 * @return True if there is rectangle intersection
	 */
	private boolean intersectRect(int r1x1, int r1y1, int r1x2, int r1y2,
			int r2x1, int r2y1, int r2x2, int r2y2) {
		if (r2x1 >= r1x2 || r2y1 >= r1y2 || r2x2 <= r1x1 || r2y2 <= r1y1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Given a rectangle that lies within the sprite in the painter's
	 * coordinates, find the X coordinate of the top left corner in the source
	 * image of the sprite
	 * 
	 * @param x1
	 *            the x coordinate of the top left of the rectangle
	 * @param y1
	 *            the y coordinate of the top left of the rectangle
	 * @param x2
	 *            the x coordinate of the bottom right of the rectangle
	 * @param y2
	 *            the y coordinate of the bottom right of the rectangle
	 * 
	 * @return the X coordinate in the source image
	 * 
	 */
	private int getImageTopLeftX(int x1, int y1, int x2, int y2) {
		int retX = 0;

		// left = this.x
		// right = this.x + this.width
		// top = this.y
		// bottom = this.y + this.height

		switch (this.t_currentTransformation) {

		case TRANS_NONE:
		case TRANS_MIRROR_ROT180:
			retX = x1 - this.x;
			break;

		case TRANS_MIRROR:
		case TRANS_ROT180:
			retX = (this.x + this.width) - x2;
			break;

		case TRANS_ROT90:
		case TRANS_MIRROR_ROT270:
			retX = y1 - this.y;
			break;

		case TRANS_ROT270:
		case TRANS_MIRROR_ROT90:
			retX = (this.y + this.height) - y2;
			break;
		}

		retX += frameCoordsX[frameSequence[sequenceIndex]];

		return retX;
	}

	/**
	 * Given a rectangle that lies within the sprite in the painter's
	 * coordinates, find the Y coordinate of the top left corner in the source
	 * image of the sprite
	 * 
	 * @param x1
	 *            the x coordinate of the top left of the rectangle
	 * @param y1
	 *            the y coordinate of the top left of the rectangle
	 * @param x2
	 *            the x coordinate of the bottom right of the rectangle
	 * @param y2
	 *            the y coordinate of the bottom right of the rectangle
	 * 
	 * @return the Y coordinate in the source image
	 * 
	 */
	private int getImageTopLeftY(int x1, int y1, int x2, int y2) {
		int retY = 0;

		// left = this.x
		// right = this.x + this.width
		// top = this.y
		// bottom = this.y + this.height

		switch (this.t_currentTransformation) {

		case TRANS_NONE:
		case TRANS_MIRROR:
			retY = y1 - this.y;
			break;

		case TRANS_ROT180:
		case TRANS_MIRROR_ROT180:
			retY = (this.y + this.height) - y2;
			break;

		case TRANS_ROT270:
		case TRANS_MIRROR_ROT270:
			retY = x1 - this.x;
			break;

		case TRANS_ROT90:
		case TRANS_MIRROR_ROT90:
			retY = (this.x + this.width) - x2;
			break;
		}

		retY += frameCoordsY[frameSequence[sequenceIndex]];

		return retY;
	}

	/**
	 * Sets the transform for this ShahSprite
	 * 
	 * @param transform
	 *            the desired transform for this ShahSprite
	 */
	private void setTransformImpl(int transform) {

		// ---

		// setTransform sets up all transformation related data structures
		// except transforming the current frame's bitmap.

		// x, y, width, height, dRefX, dRefY,
		// collisionRectX, collisionRectY, collisionRectWidth,
		// collisionRectHeight, t_currentTransformation,
		// t_bufferImage

		// The actual tranformed frame is drawn at paint time.

		// ---

		// update top-left corner position
		this.x = this.x
				+ getTransformedPtX(dRefX, dRefY, this.t_currentTransformation)
				- getTransformedPtX(dRefX, dRefY, transform);

		this.y = this.y
				+ getTransformedPtY(dRefX, dRefY, this.t_currentTransformation)
				- getTransformedPtY(dRefX, dRefY, transform);

		// Calculate transformed sprites collision rectangle
		// and transformed width and height
		computeTransformedBounds(transform);

		// set the current transform to be the one requested
		t_currentTransformation = transform;

	}

	/**
	 * Calculate transformed sprites collision rectangle and transformed width
	 * and height
	 * 
	 * @param transform
	 *            the desired transform for this <code>ShahSprite</code>
	 */
	private void computeTransformedBounds(int transform) {
		switch (transform) {

		case TRANS_NONE:

			t_collisionRectX = collisionRectX;
			t_collisionRectY = collisionRectY;
			t_collisionRectWidth = collisionRectWidth;
			t_collisionRectHeight = collisionRectHeight;
			this.width = srcFrameWidth;
			this.height = srcFrameHeight;

			break;

		case TRANS_MIRROR:

			// flip across vertical

			// NOTE: top left x and y coordinate must reflect the transformation
			// performed around the reference point

			// the X-offset of the reference point from the top left corner
			// changes.
			t_collisionRectX = srcFrameWidth
					- (collisionRectX + collisionRectWidth);

			t_collisionRectY = collisionRectY;
			t_collisionRectWidth = collisionRectWidth;
			t_collisionRectHeight = collisionRectHeight;

			// the Y-offset of the reference point from the top left corner
			// remains the same,
			// top left X-co-ordinate changes

			this.width = srcFrameWidth;
			this.height = srcFrameHeight;

			break;

		case TRANS_MIRROR_ROT180:

			// flip across horizontal

			// NOTE: top left x and y coordinate must reflect the transformation
			// performed around the reference point

			// the Y-offset of the reference point from the top left corner
			// changes
			t_collisionRectY = srcFrameHeight
					- (collisionRectY + collisionRectHeight);

			t_collisionRectX = collisionRectX;
			t_collisionRectWidth = collisionRectWidth;
			t_collisionRectHeight = collisionRectHeight;

			// width and height are as before
			this.width = srcFrameWidth;
			this.height = srcFrameHeight;

			// the X-offset of the reference point from the top left corner
			// remains the same.
			// top left Y-co-ordinate changes

			break;

		case TRANS_ROT90:

			// NOTE: top left x and y coordinate must reflect the transformation
			// performed around the reference point

			// the bottom-left corner of the rectangle becomes the
			// top-left when rotated 90.

			// both X- and Y-offset to the top left corner may change

			// update the position information for the collision rectangle

			t_collisionRectX = srcFrameHeight
					- (collisionRectHeight + collisionRectY);
			t_collisionRectY = collisionRectX;

			t_collisionRectHeight = collisionRectWidth;
			t_collisionRectWidth = collisionRectHeight;

			// set width and height
			this.width = srcFrameHeight;
			this.height = srcFrameWidth;

			break;

		case TRANS_ROT180:

			// NOTE: top left x and y coordinate must reflect the transformation
			// performed around the reference point

			// width and height are as before

			// both X- and Y- offsets from the top left corner may change

			t_collisionRectX = srcFrameWidth
					- (collisionRectWidth + collisionRectX);
			t_collisionRectY = srcFrameHeight
					- (collisionRectHeight + collisionRectY);

			t_collisionRectWidth = collisionRectWidth;
			t_collisionRectHeight = collisionRectHeight;

			// set width and height
			this.width = srcFrameWidth;
			this.height = srcFrameHeight;

			break;

		case TRANS_ROT270:

			// the top-right corner of the rectangle becomes the
			// top-left when rotated 270.

			// both X- and Y-offset to the top left corner may change

			// update the position information for the collision rectangle

			t_collisionRectX = collisionRectY;
			t_collisionRectY = srcFrameWidth
					- (collisionRectWidth + collisionRectX);

			t_collisionRectHeight = collisionRectWidth;
			t_collisionRectWidth = collisionRectHeight;

			// set width and height
			this.width = srcFrameHeight;
			this.height = srcFrameWidth;

			break;

		case TRANS_MIRROR_ROT90:

			// both X- and Y- offset from the top left corner may change

			// update the position information for the collision rectangle

			t_collisionRectX = srcFrameHeight
					- (collisionRectHeight + collisionRectY);
			t_collisionRectY = srcFrameWidth
					- (collisionRectWidth + collisionRectX);

			t_collisionRectHeight = collisionRectWidth;
			t_collisionRectWidth = collisionRectHeight;

			// set width and height
			this.width = srcFrameHeight;
			this.height = srcFrameWidth;

			break;

		case TRANS_MIRROR_ROT270:

			// both X- and Y- offset from the top left corner may change

			// update the position information for the collision rectangle

			t_collisionRectY = collisionRectX;
			t_collisionRectX = collisionRectY;

			t_collisionRectHeight = collisionRectWidth;
			t_collisionRectWidth = collisionRectHeight;

			// set width and height
			this.width = srcFrameHeight;
			this.height = srcFrameWidth;

			break;

		default:
			// INVALID TRANSFORMATION!
			throw new IllegalArgumentException();

		}
	}

	/**
	 * Given the x and y offsets off a pixel from the top left corner, in an
	 * untransformed sprite, calculates the x coordinate of the pixel when the
	 * same sprite is transformed, with the coordinates of the top-left pixel of
	 * the transformed sprite as (0,0).
	 * 
	 * @param x
	 *            Horizontal offset within the untransformed sprite
	 * @param y
	 *            Vertical offset within the untransformed sprite
	 * @param transform
	 *            transform for the sprite
	 * @return The x-offset, of the coordinates of the pixel, with the top-left
	 *         corner as 0 when transformed.
	 */
	int getTransformedPtX(int x, int y, int transform) {

		int t_x = 0;

		switch (transform) {

		case TRANS_NONE:
			t_x = x;
			break;
		case TRANS_MIRROR:
			t_x = srcFrameWidth - x - 1;
			break;
		case TRANS_MIRROR_ROT180:
			t_x = x;
			break;
		case TRANS_ROT90:
			t_x = srcFrameHeight - y - 1;
			break;
		case TRANS_ROT180:
			t_x = srcFrameWidth - x - 1;
			break;
		case TRANS_ROT270:
			t_x = y;
			break;
		case TRANS_MIRROR_ROT90:
			t_x = srcFrameHeight - y - 1;
			break;
		case TRANS_MIRROR_ROT270:
			t_x = y;
			break;
		default:
			// INVALID TRANSFORMATION!
			throw new IllegalArgumentException();

		}

		return t_x;

	}

	/**
	 * Given the x and y offsets off a pixel from the top left corner, in an
	 * untransformed sprite, calculates the y coordinate of the pixel when the
	 * same sprite is transformed, with the coordinates of the top-left pixel of
	 * the transformed sprite as (0,0).
	 * 
	 * @param x
	 *            Horizontal offset within the untransformed sprite
	 * @param y
	 *            Vertical offset within the untransformed sprite
	 * @param transform
	 *            transform for the sprite
	 * @return The y-offset, of the coordinates of the pixel, with the top-left
	 *         corner as 0 when transformed.
	 */
	int getTransformedPtY(int x, int y, int transform) {

		int t_y = 0;

		switch (transform) {

		case TRANS_NONE:
			t_y = y;
			break;
		case TRANS_MIRROR:
			t_y = y;
			break;
		case TRANS_MIRROR_ROT180:
			t_y = srcFrameHeight - y - 1;
			break;
		case TRANS_ROT90:
			t_y = x;
			break;
		case TRANS_ROT180:
			t_y = srcFrameHeight - y - 1;
			break;
		case TRANS_ROT270:
			t_y = srcFrameWidth - x - 1;
			break;
		case TRANS_MIRROR_ROT90:
			t_y = srcFrameWidth - x - 1;
			break;
		case TRANS_MIRROR_ROT270:
			t_y = x;
			break;
		default:
			// INVALID TRANSFORMATION!
			throw new IllegalArgumentException();
		}

		return t_y;

	}


	public int getDstRectWidth(){
//		return (int)(srcFrameWidth*GlobalVariables.xScale_factor);
        return (int)(srcFrameWidth);
	}
	public RectF getDstRect(){
//        return  new Rect(getX(),getY(),getDstRectWidth(),getDstRectHeight());

        return  new RectF(getX(),getY(),getX()+getDstRectWidth(),getY()+getDstRectHeight());
    }
	public int getDstRectHeight(){
//		return (int)(srcFrameHeight*GlobalVariables.yScale_factor);
        return (int)(srcFrameHeight);
	}
	// --- member variables

	/**
	 * If this bit is set, it denotes that the transform causes the axes to be
	 * interchanged
	 */
	private static final int INVERTED_AXES = 0x4;

	/**
	 * If this bit is set, it denotes that the transform causes the x axis to be
	 * flipped.
	 */
	private static final int X_FLIP = 0x2;

	/**
	 * If this bit is set, it denotes that the transform causes the y axis to be
	 * flipped.
	 */
	private static final int Y_FLIP = 0x1;

	/**
	 * Bit mask for channel value in ARGB pixel.
	 */
	private static final int ALPHA_BITMASK = 0xff000000;

	/**
	 * Source image
	 */
	public Bitmap sourceImage;

	/**
	 * The number of frames
	 */
	int numberFrames; // = 0;

	/**
	 * list of X coordinates of individual frames
	 */
	int[] frameCoordsX;
	/**
	 * list of Y coordinates of individual frames
	 */
	int[] frameCoordsY;

	/**
	 * Width of each frame in the source image
	 */
	int srcFrameWidth;

	/**
	 * Height of each frame in the source image
	 */
	int srcFrameHeight;

	/**
	 * The sequence in which to display the ShahSprite frames
	 */
	int[] frameSequence;

	/**
	 * The sequence index
	 */
	private int sequenceIndex; // = 0

	/**
	 * Set to true if custom sequence is used.
	 */
	private boolean customSequenceDefined; // = false;

	// -- reference point
	/**
	 * Horizontal offset of the reference point from the top left of the sprite.
	 */
	int dRefX; // =0

	/**
	 * Vertical offset of the reference point from the top left of the sprite.
	 */
	int dRefY; // =0

	// --- collision rectangle

	/**
	 * Horizontal offset of the top left of the collision rectangle from the top
	 * left of the sprite.
	 */
	int collisionRectX; // =0

	/**
	 * Vertical offset of the top left of the collision rectangle from the top
	 * left of the sprite.
	 */
	int collisionRectY; // =0

	/**
	 * Width of the bounding rectangle for collision detection.
	 */
	int collisionRectWidth;

	/**
	 * Height of the bounding rectangle for collision detection.
	 */
	int collisionRectHeight;

	// --- transformation(s)
	// --- values that may change on setting transformations
	// start with t_

	/**
	 * The current transformation in effect.
	 */
	int t_currentTransformation;

	/**
	 * Horizontal offset of the top left of the collision rectangle from the top
	 * left of the sprite.
	 */
	int t_collisionRectX;

	/**
	 * Vertical offset of the top left of the collision rectangle from the top
	 * left of the sprite.
	 */
	int t_collisionRectY;

	/**
	 * Width of the bounding rectangle for collision detection, with the current
	 * transformation in effect.
	 */
	int t_collisionRectWidth;

	/**
	 * Height of the bounding rectangle for collision detection, with the
	 * current transformation in effect.
	 */
	int t_collisionRectHeight;

    /*@Override
    public void recycle() {
        if (!this.sourceImage.isRecycled()) {
            this.sourceImage.recycle();
        }
    }*/
}
