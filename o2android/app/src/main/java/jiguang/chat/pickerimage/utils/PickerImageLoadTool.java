package jiguang.chat.pickerimage.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;


public class PickerImageLoadTool {

	private static ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static ImageLoader getImageLoader(){
		return imageLoader;
	}
	
	public static boolean checkImageLoader(){
		return imageLoader.isInited();
	}
	
	public static void disPlay(String uri, ImageAware imageAware, int defaultPic){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(defaultPic)
		.showImageForEmptyUri(defaultPic)
		.showImageOnFail(defaultPic)
		.cacheInMemory(true)
		.cacheOnDisc(false)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new SimpleBitmapDisplayer())
		.build();

		imageLoader.displayImage(uri, imageAware, options);
	}
	
	public static void clear(){
		imageLoader.clearMemoryCache();		
//		imageLoader.clearDiscCache();
	}
	
	public static void resume(){
		imageLoader.resume();
	}
	
	public static void pause(){
		imageLoader.pause();
	}
	
	public static void stop(){
		imageLoader.stop();
	}
	
	public static void destroy() {
		imageLoader.destroy();
	}
}
