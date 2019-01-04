package jiguang.chat.activity.historyfile.view;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NativeImageLoaderView {
	private static final String TAG = NativeImageLoaderView.class.getSimpleName();
	private static NativeImageLoaderView mInstance = new NativeImageLoaderView();
	private static LruCache<String, Bitmap> mMemoryCache;
	private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);

	private NativeImageLoaderView(){
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory());

		final int cacheSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	public static NativeImageLoaderView getInstance(){
		return mInstance;
	}
	
	

	public Bitmap loadNativeImage(final String path, final NativeImageCallBack mCallBack){
		return this.loadNativeImage(path, null, mCallBack);
	}
	

	public Bitmap loadNativeImage(final String path, final Point mPoint, final NativeImageCallBack mCallBack){
		Bitmap bitmap = getBitmapFromMemCache(path);
		
		final Handler mHander = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mCallBack.onImageLoader((Bitmap)msg.obj, path);
			}
			
		};
		
		if(bitmap == null){
			mImageThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint == null ? 0: mPoint.x, mPoint == null ? 0: mPoint.y);
					Message msg = mHander.obtainMessage();
					msg.obj = mBitmap;
					mHander.sendMessage(msg);
					
					addBitmapToMemoryCache(path, mBitmap);
				}
			});
		}
		return bitmap;
		
	}

	
	

	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}


	private Bitmap getBitmapFromMemCache(String key) {
		
		Bitmap bitmap = mMemoryCache.get(key);
		
		if(bitmap != null){
			Log.i(TAG, "get image for MemCache , path = " + key);
		}
		
		return bitmap;
	}
	
	

	private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		options.inJustDecodeBounds = false;
		
		
		Log.e(TAG, "get Iamge form file,  path = " + path);
		
		return BitmapFactory.decodeFile(path, options);
	}
	
	

	private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		int inSampleSize = 1;
		if(viewWidth == 0 || viewWidth == 0){
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		
		if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);
			
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}
	

	public interface NativeImageCallBack{

		public void onImageLoader(Bitmap bitmap, String path);
	}
}
