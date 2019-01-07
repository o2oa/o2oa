package jiguang.chat.pickerimage.utils;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;


public class RotateImageViewAware implements ImageAware {

	protected Reference<ImageView> imageViewRef;
	protected boolean checkActualViewSize;
	private String path;

	public RotateImageViewAware(ImageView imageView, String path) {
		this(imageView, false);
		this.path = path;
	}

	public RotateImageViewAware(ImageView imageView, boolean checkActualViewSize) {
		this.imageViewRef = new WeakReference<ImageView>(imageView);
		this.checkActualViewSize = checkActualViewSize;
	}

	@Override
	public int getWidth() {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			final ViewGroup.LayoutParams params = imageView.getLayoutParams();
			int width = 0;
			if (checkActualViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
				width = imageView.getWidth(); // Get actual image width
			}
			if (width <= 0 && params != null) width = params.width; // Get layout width parameter
			if (width <= 0) width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check maxWidth parameter
			return width;
		}
		return 0;
	}

	@Override
	public int getHeight() {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			final ViewGroup.LayoutParams params = imageView.getLayoutParams();
			int height = 0;
			if (checkActualViewSize && params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
				height = imageView.getHeight(); // Get actual image height
			}
			if (height <= 0 && params != null) height = params.height; // Get layout height parameter
			if (height <= 0) height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check maxHeight parameter
			return height;
		}
		return 0;
	}

	@Override
	public ViewScaleType getScaleType() {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			return ViewScaleType.fromImageView(imageView);
		}
		return null;
	}

	@Override
	public ImageView getWrappedView() {
		return imageViewRef.get();
	}

	@Override
	public boolean isCollected() {
		return imageViewRef.get() == null;
	}

	@Override
	public int getId() {
		ImageView imageView = imageViewRef.get();
		return imageView == null ? super.hashCode() : imageView.hashCode();
	}

	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;
	}

	@Override
	public boolean setImageDrawable(Drawable drawable) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setImageDrawable(drawable);
			return true;
		}
		return false;
	}

	@Override
	public boolean setImageBitmap(Bitmap bitmap) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setImageBitmap(BitmapUtil.reviewPicRotate(bitmap, path));
		}
		return false;
	}
}
