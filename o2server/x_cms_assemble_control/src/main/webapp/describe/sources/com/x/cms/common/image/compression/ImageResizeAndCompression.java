package com.x.cms.common.image.compression;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

  
/**
 * 图片压缩工具类 提供的方法中可以设定生成的 缩略图片的大小尺寸、压缩尺寸的比例、图片的质量等
 */
public class ImageResizeAndCompression {  
  
    /** 
     * * 图片文件读取 
     *  
     * @param srcImgPath 
     * @return 
     */  
    private static BufferedImage InputImage(String srcImgPath) throws RuntimeException {  
  
        BufferedImage srcImage = null;
        FileInputStream in = null;
        try {  
            // 构造BufferedImage对象  
            File file = new File(srcImgPath);  
            in = new FileInputStream(file);  
            byte[] b = new byte[5];  
            in.read(b);  
            srcImage = javax.imageio.ImageIO.read(file);  
        } catch (IOException e) {  
        	e.printStackTrace();  
        	throw new RuntimeException("读取图片文件出错！", e);
        } finally {
        	if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException("读取图片文件出错！", e);
				}
			}
        }
        return srcImage;  
    }  
  
    /** 
     * * 将图片按照指定的图片尺寸、源图片质量压缩(默认质量为1) 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param new_w 
     *            :压缩后的图片宽 
     * @param new_h 
     *            :压缩后的图片高 
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            int new_w, int new_h) {  
        resize(srcImgPath, outImgPath, new_w, new_h, 1F);  
    }  
  
    /** 
     * 将图片按照指定的尺寸比例、源图片质量压缩(默认质量为1) 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param ratio 
     *            :压缩后的图片尺寸比例 
     * @param per 
     *            :百分比 
     */  
    public static void resize(String srcImgPath, String outImgPath,   float ratio) {  
        resize(srcImgPath, outImgPath, ratio, 1F);  
    }  
  
    /** 
     * 将图片按照指定长或者宽的最大值来压缩图片(默认质量为1) 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param maxLength 
     *            :长或者宽的最大值 
     * @param per 
     *            :图片质量 
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            int maxLength) {  
        resize(srcImgPath, outImgPath, maxLength, 1F);  
    }  
  
    /** 
     * * 将图片按照指定的图片尺寸、图片质量压缩 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param new_w 
     *            :压缩后的图片宽 
     * @param new_h 
     *            :压缩后的图片高 
     * @param per 
     *            :百分比 
     * @author cevencheng
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            int new_w, int new_h, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        // 根据原图的大小生成空白画布  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  BufferedImage.TYPE_INT_RGB);  
        // 在新的画布上生成原图的缩略图  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 调用方法输出图片文件  
        outImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * * 将图片按照指定的尺寸比例、图片质量压缩 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param ratio 
     *            :压缩后的图片尺寸比例 
     * @param per 
     *            :百分比 
     * @author cevencheng
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            float ratio, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        int new_w = 0;  
        // 新图的宽  
        int new_h = 0;  
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  
                BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 从原图上取颜色绘制新图g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 根据图片尺寸压缩比得到新图的尺寸new_w = (int) Math.round(old_w * ratio);  
        new_h = (int) Math.round(old_h * ratio);  
        BufferedImage newImg = new BufferedImage(new_w, new_h,  
                BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 调用方法输出图片文件OutImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * <b>
     * 指定长或者宽的最大值来压缩图片
     * 	推荐使用此方法 
     * </b>
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param maxLength 
     *            :长或者宽的最大值 
     * @param per 
     *            :图片质量 
     * @author cevencheng
     */  
    public static void resize(String srcImgPath, String outImgPath, int maxLength, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        int new_w = 0;  
        // 新图的宽  
        int new_h = 0;  
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  
                BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 从原图上取颜色绘制新图  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 根据图片尺寸压缩比得到新图的尺寸  
        if (old_w > old_h) {  
            // 图片要缩放的比例  
            new_w = maxLength;  
            new_h = (int) Math.round(old_h * ((float) maxLength / old_w));  
        } else {  
            new_w = (int) Math.round(old_w * ((float) maxLength / old_h));  
            new_h = maxLength;  
        }  
        BufferedImage newImg = new BufferedImage(new_w, new_h,  
                BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 调用方法输出图片文件  
        outImage(outImgPath, newImg, per);  
    }  
    
    /**
     * 将图片压缩成指定宽度， 高度等比例缩放
     * 
     * @param srcImgPath
     * @param outImgPath
     * @param width
     * @param per
     */
    public static void resizeFixedWidth(String srcImgPath, String outImgPath,  
    		int width, float per) {  
    	// 得到图片  
    	BufferedImage src = InputImage(srcImgPath);  
    	int old_w = src.getWidth();  
    	// 得到源图宽  
    	int old_h = src.getHeight();  
    	// 得到源图长  
    	int new_w = 0;  
    	// 新图的宽  
    	int new_h = 0;  
    	// 新图的长  
    	BufferedImage tempImg = new BufferedImage(old_w, old_h,  
    			BufferedImage.TYPE_INT_RGB);  
    	Graphics2D g = tempImg.createGraphics();  
    	g.setColor(Color.white);  
    	// 从原图上取颜色绘制新图  
    	g.fillRect(0, 0, old_w, old_h);  
    	g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
    	g.dispose();  
    	// 根据图片尺寸压缩比得到新图的尺寸  
    	if (old_w > old_h) {  
    		// 图片要缩放的比例  
    		new_w = width;  
    		new_h = (int) Math.round(old_h * ((float) width / old_w));  
    	} else {  
    		new_w = (int) Math.round(old_w * ((float) width / old_h));  
    		new_h = width;  
    	}  
    	BufferedImage newImg = new BufferedImage(new_w, new_h,  
    			BufferedImage.TYPE_INT_RGB);  
    	newImg.getGraphics().drawImage(  
    			tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
    			0, null);  
    	// 调用方法输出图片文件  
    	outImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * * 将图片文件输出到指定的路径，并可设定压缩质量 
     *  
     * @param outImgPath 
     * @param newImg 
     * @param per 
     * @author cevencheng
     */  
    private static void outImage( String outImgPath, BufferedImage newImg, float per ) {  
        // 判断输出的文件夹路径是否存在，不存在则创建  
        File file = new File(outImgPath);  
        if (!file.getParentFile().exists()) {  
            file.getParentFile().mkdirs();  
        }
        // 输出到文件流
        FileOutputStream fos = null;
        try {  
            fos = new FileOutputStream( outImgPath );
            ImageIO.write( newImg,  "jpeg" , file );
            newImg.flush();  
        } catch (Exception e) { 
        	throw new RuntimeException(e);
        } finally {
        	if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
        }
    }  
    
   /**
    * 将图片文件输出到指定的输出流，并可设定压缩质量 
    * @param dest
    * @param newImg
    * @param per
    */
    private static void outImage( OutputStream dest, BufferedImage newImg, float per ) {  
        try { 
            ImageIO.write( newImg,  "jpeg" , dest );
        } catch (Exception e) { 
        	throw new RuntimeException(e);
        }
    }  
  
    /**
     * 图片剪切工具方法
     * 
     * @param srcfile 源图片
     * @param outfile 剪切之后的图片
     * @param x 剪切顶点 X 坐标
     * @param y 剪切顶点 Y 坐标
     * @param width 剪切区域宽度
     * @param height 剪切区域高度
     * 
     * @throws IOException
     * @author cevencheng
     */
	public static void cut( File srcfile, File outfile, int x, int y, int width, int height ) throws IOException {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			// 读取图片文件
			is = new FileInputStream(srcfile);
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = it.next();
			// 获取图片流
			iis = ImageIO.createImageInputStream(is);

			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
			Rectangle rect = new Rectangle(x, y, width, height);

			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);

			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);

			// 保存新图片
			ImageIO.write(bi, "jpg", outfile);
		} finally {
			if (is != null) {
				is.close();
			}
			if (iis != null) {
				iis.close();
			}
		}
    }
    
	
	/**
     * 先压缩后剪切工具方法
     * 
     * @param srcfile 源图片
     * @param outfile 剪切之后的图片
     * @param width 剪切区域宽度
     * @param height 剪切区域高度
     * 
     * @throws IOException
     * @author cevencheng
     */
	public static void cut( String srcImgPath, String outImgPath, int width, int height ) throws IOException {
        BufferedImage src = InputImage( srcImgPath );  // 得到图片对象
        int old_w = src.getWidth();  //得到源图宽  
        int old_h = src.getHeight();  //得到源图高
        int new_w = width;
        int new_h = height;
        File outfile = new File(outImgPath);
        BufferedImage tempImg = new BufferedImage( old_w, old_h, BufferedImage.TYPE_INT_RGB );  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor( Color.white );  
        // 从原图上取颜色绘制新图
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage( src, 0, 0, old_w, old_h, Color.white, null );  
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸  
        if( ( old_w*1.0 )/new_w < ( new_h*1.0 )/new_h ){//纵向缩放比例大
            if( old_w > new_w ){//实际的图片宽度，大于预期的缩放宽度，需要进行高度按比例缩小
            	new_h = Integer.parseInt( new java.text.DecimalFormat("0").format(old_h * new_w/(old_w*1.0)) );
            }
        } else {//横向绽放比例大
            if(old_h > new_h){
            	new_w = Integer.parseInt(new java.text.DecimalFormat("0").format(old_w * new_h/(old_h*1.0)));
            }
        }        
         
        BufferedImage newImg = new BufferedImage( new_w, new_h, BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(   tempImg.getScaledInstance( new_w, new_h, Image.SCALE_SMOOTH), 0,  0, null );
        
        //准备进行裁剪
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 调用方法输出图片文件
        outImage( baos, newImg, 1 );
		
        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
		ImageInputStream iis = null;
		try {
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = it.next();
			// 获取图片流
			iis = ImageIO.createImageInputStream(is);
			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);
			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();
			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
			int x=0, y=0;
//			if( new_w > width ){ //水平中间截取
//				x = ( new_w-width )/2;
//			}else{
//				y = ( new_h-height )/2;
//			}
			Rectangle rect = new Rectangle( x, y, width, height);

			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);
			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);
			// 保存新图片
			ImageIO.write(bi, "jpg", outfile);
		} finally {
			if (is != null) {
				is.close();
			}
			if (iis != null) {
				iis.close();
			}
		}
    }
	
	public static BufferedImage cut( BufferedImage src, Integer width, Integer height ) throws IOException {
	        int old_w = src.getWidth();  //得到源图宽  
	        int old_h = src.getHeight();  //得到源图高
	        int new_w = width;
	        int new_h = height;
	        BufferedImage tempImg = new BufferedImage( old_w, old_h, BufferedImage.TYPE_INT_RGB );  
	        Graphics2D g = tempImg.createGraphics();  
	        g.setColor( Color.white );  
	        // 从原图上取颜色绘制新图
	        g.fillRect(0, 0, old_w, old_h);  
	        g.drawImage( src, 0, 0, old_w, old_h, Color.white, null );  
	        g.dispose();
	        // 根据图片尺寸压缩比得到新图的尺寸  
	        if( ( old_w*1.0 )/new_w < ( new_h*1.0 )/new_h ){//纵向缩放比例大
	            if( old_w > new_w ){//实际的图片宽度，大于预期的缩放宽度，需要进行高度按比例缩小
	            	new_h = Integer.parseInt( new java.text.DecimalFormat("0").format(old_h * new_w/(old_w*1.0)) );
	            }
	        } else {//横向绽放比例大
	            if(old_h > new_h){
	            	new_w = Integer.parseInt(new java.text.DecimalFormat("0").format(old_w * new_h/(old_h*1.0)));
	            }
	        }        
	         
	        BufferedImage newImg = new BufferedImage( new_w, new_h, BufferedImage.TYPE_INT_RGB);  
	        newImg.getGraphics().drawImage(   tempImg.getScaledInstance( new_w, new_h, Image.SCALE_SMOOTH), 0,  0, null );
	        
	        //准备进行裁剪
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        // 调用方法输出图片文件
	        outImage( baos, newImg, 1 );
			
	        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
			ImageInputStream iis = null;
			try {
				/*
				 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
				 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
				 */
				Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
				ImageReader reader = it.next();
				// 获取图片流
				iis = ImageIO.createImageInputStream(is);
				/*
				 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
				 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
				 */
				reader.setInput(iis, true);
				/*
				 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
				 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
				 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
				 */
				ImageReadParam param = reader.getDefaultReadParam();
				/*
				 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
				 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
				 */
				int x=0, y=0;
				if( new_w > width ){ //水平中间截取
					x = ( new_w-width )/2;
				}else{
					y = ( new_h-height )/2;
				}
				Rectangle rect = new Rectangle( x, y, width, height);

				// 提供一个 BufferedImage，将其用作解码像素数据的目标。
				param.setSourceRegion(rect);
				/*
				 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
				 * BufferedImage 返回。
				 */
				BufferedImage bi = reader.read(0, param);
				// 保存新图片
				return bi;
			} finally {
				if (is != null) {
					is.close();
				}
				if (iis != null) {
					iis.close();
				}
			}
	}
	
    public static void main( String args[] ) throws Exception {  
        String srcImg = "d:/Test.png";  
        String tarDir = "d:/Test_400_400.png";
        long startTime = new Date().getTime(); 
        cut( srcImg, tarDir, 400, 250 );
        System.out.println(new Date().getTime() - startTime);  
    }  
}  

	    			