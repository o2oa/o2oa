package jiguang.chat.pickerimage.utils;

import android.opengl.GLES10;

public class SampleSizeUtil {
	
	public static int calculateSampleSize(String imagePath, int totalPixel) {
		int[] bound = BitmapDecoder.decodeBound(imagePath);
		return calculateSampleSize(bound[0], bound[1], totalPixel);
	}
	
	public static int calculateSampleSize(int width, int height, int totalPixel) {
    	int ratio = 1;

        if (width > 0 && height > 0) {
        	ratio = (int) Math.sqrt((float) (width * height) / totalPixel);
        	if (ratio < 1) {
        		ratio = 1;
        	}        
        }

        return ratio;    
	}
	

	public static int calculateSampleSize(int width, int height, int reqWidth, int reqHeight) {
		// can't proceed
		if (width <= 0 || height <= 0) {
			return 1;
		}
		// can't proceed
		if (reqWidth <= 0 && reqHeight <= 0) {
			return 1;
		} else if (reqWidth <= 0) {
			reqWidth = (int) (width * reqHeight / (float)height + 0.5f) ;
		} else if (reqHeight <= 0) {
			reqHeight = (int) (height * reqWidth / (float)width + 0.5f);
		}
		
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
	
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee a final image
			// with both dimensions larger than or equal to the requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			if (inSampleSize == 0) {
				inSampleSize = 1;
			}
			
			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).
	
			final float totalPixels = width * height;
			
			// Anything more than 2x the requested pixels we'll sample down
			// further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;
	
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}			
		}
		
		return inSampleSize;
	}
	
	public static final int adjustSampleSizeWithTexture(int sampleSize, int width, int height) {
		int textureSize = getTextureSize();
		
		if ((textureSize > 0) && ((width > sampleSize) || (height > sampleSize))) {
			while ((width / (float)sampleSize) > textureSize || (height / (float)sampleSize) > textureSize) {
				sampleSize++;
			}
			
			// 2的指数对齐
			sampleSize = SampleSizeUtil.roundup2n(sampleSize);
		}

		return sampleSize;
	}
	
	private static int textureSize = 0;
	//存在第二次拿拿不到的情况，所以把拿到的数据用一个static变量保存下来
	public static final int getTextureSize() {
		if (textureSize > 0) {
			return textureSize;
		}
		
		int[] params = new int[1];
		GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, params, 0);
		textureSize = params[0];
		
		return textureSize;		
	}
	
	// 将x向上对齐到2的幂指数
		private static final int roundup2n(int x) {
			if ((x & (x - 1)) == 0) {
				return x;
			}
			int pos = 0;
			while (x > 0) {
				x >>= 1;
				++pos;
			}
			return 1 << pos;
		}
}
