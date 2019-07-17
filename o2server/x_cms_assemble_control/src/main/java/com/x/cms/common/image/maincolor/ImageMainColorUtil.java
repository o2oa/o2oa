package com.x.cms.common.image.maincolor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.x.base.core.project.tools.ListTools;

public class ImageMainColorUtil {
	
	BufferedImage image = null;
	
	public int max(int a, int b, int c) {
		int result = 0;
		if (a >= b && a >= c) {
			result = 1;
		} else if (b >= a && b >= c) {
			result = 2;
		} else if (c >= b && c >= a) {
			result = 3;
		}
		return result;
	}

	// 得到某位置的像素RBG
	public Point getRGB(int x, int y) {
		Point point = new Point();
		int pixel = image.getRGB(x, y);
		point.R = (pixel & 0xff0000) >> 16;
		point.G = (pixel & 0xff00) >> 8;
		point.B = (pixel & 0xff);
		return point;
	}

	public boolean isEnd( List<PointGroup> rootPoint ) {
		Point oldpoint = new Point();
		Point newpoint = new Point();
		for (PointGroup pointGroup : rootPoint) {
			oldpoint.add(pointGroup.point);
			newpoint.add(pointGroup.getNewRGB());
			pointGroup.point = pointGroup.getNewRGB();
		}
		if (oldpoint.equals(newpoint)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Point> getThreeFamilly( List<PointGroup> rootPoint ) {
		List<Point> pointList = new ArrayList<Point>();
		// 去除与白色相近的颜色
		Iterator<PointGroup> it = rootPoint.iterator();
		int removedCount_white = 0;
		int removedCount_black = 0;
		while (it.hasNext()) {
			PointGroup i = it.next();
			if (i.point.colorDistance( new Point(255, 255, 255) ) < 10) {
				//去掉接近白色的色值
				it.remove();
				removedCount_white ++;
			}else if (i.point.colorDistance( new Point(0, 0, 0) ) < 10) {
				//去掉近黑色的色值
				it.remove();
				removedCount_black ++;
			}
		}
		// 根据RGB的总和对颜色排序
		Collections.sort( rootPoint, new pointCampare() );
		// 如果颜色小于等于3个，直接输出
		if ( rootPoint.size() <= 3) {
			for (int i = 0; i < rootPoint.size(); i++ ) {
				pointList.add( rootPoint.get(i).point );
			}
			return pointList;
		}

		int index = 1;
		int min = 100000;
		// 得到3个最相近的颜色
		for (int i = 1; i < rootPoint.size() - 1; i++) {
			int temp = rootPoint.get(i + 1).point.colorDistance(rootPoint.get(i).point)
					+ rootPoint.get(i).point.colorDistance(rootPoint.get(i - 1).point);
			if (temp < min) {
				min = temp;
				index = i;
			}
		}
		// 根据R-G-B中最大的排序
		int max = 0;
		// 得到RGB中最大的分量
		if (rootPoint.get(index).point.R >= rootPoint.get(index).point.G && rootPoint.get(index).point.R >= rootPoint.get(index).point.B) {
			max = 1;
		} else if (rootPoint.get(index).point.G >= rootPoint.get(index).point.R && rootPoint.get(index).point.G >= rootPoint.get(index).point.B) {
			max = 2;
		} else {
			max = 3;
		}

		switch (max) {
		// 根据R值排序
		case 1:
			int temp = max(rootPoint.get(index - 1).point.R, rootPoint.get(index).point.R, rootPoint.get(index + 1).point.R);
			pointList.add(rootPoint.get(index - 2 + temp).point);
			if (temp == 1) {
				if (rootPoint.get(index).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index).point);
				}
			}
			if (temp == 2) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			if (temp == 3) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index).point);
				} else {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			break;
		// 根据G值排序
		case 2:
			int temp1 = max(rootPoint.get(index - 1).point.G, rootPoint.get(index).point.G,
					rootPoint.get(index + 1).point.G);
			pointList.add(rootPoint.get(index - 2 + temp1).point);
			if (temp1 == 1) {
				if (rootPoint.get(index).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index).point);
				}
			}
			if (temp1 == 2) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			if (temp1 == 3) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index).point);
				} else {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			break;
		// 根据B值排序
		case 3:
			int temp3 = max(rootPoint.get(index - 1).point.B, rootPoint.get(index).point.B, rootPoint.get(index + 1).point.B);
			pointList.add(rootPoint.get(index - 2 + temp3).point);
			if (temp3 == 1) {
				if (rootPoint.get(index).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index).point);
				}
			}
			if (temp3 == 2) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			if (temp3 == 3) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index).point);
				} else {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			break;
		default:
			break;
		}
		if( ListTools.isEmpty( pointList ) ){
			if( removedCount_white > removedCount_black ){
				pointList.add( new Point(220,220,220) );
			}else{
				pointList.add( new Point(65,66,67) );
			}
		}
		return pointList;
	}

	/**
	 * 根据图片得到配色方案
	 * 
	 * @param image
	 *            bufferImage
	 * @param rootPointNum
	 *            种子点的个数
	 * @param colorNum
	 *            得到的色调个数
	 * @return
	 */
	public List<String> getColorSolution( BufferedImage image, int rootPointNum, int colorNum) {
		this.image = image;
		int width = image.getWidth();
		int height = image.getHeight();
		// 定义最大迭代次数
		int maxIterater = 5000000;
		int offsetWidth = (int) ( width / 32 );
		int offsetHeight = (int) ( height / 32 );

		// 随机得到32*32个点，在其中取rootPointNum个点，取RGB值做为种子点
		List<PointGroup> rootPoint = new ArrayList<PointGroup>();		
		for( int i = 0; i < width; i += offsetWidth ){
			for ( int j = 0; j < height; j += offsetHeight ) {
				PointGroup pg = new PointGroup();
				pg.point = getRGB( i, j );
				rootPoint.add( pg );
			}
		}			
		// 设置种子点集群初始阈值
		int threshold = 6;
		// 当当前的种子点个数大于用户设置的上限时 提高阈值
		while ( rootPoint.size() > rootPointNum ) {
			for (int i = 0; i < rootPoint.size(); i++){
				for (int j = i + 1; j < rootPoint.size(); j++) {
					if ( rootPoint.get(i).point.colorDistance(rootPoint.get(j).point) < threshold ) {
						rootPoint.remove(j);
					}
				}
			}
			threshold += 1;
		}
		int count = 0;
		do {
			// 遍历图片所有像素点，将每个点都加入其中一个种子点
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++) {
					Point point = getRGB(i, j);
					int index = 0;
					int dis = 10000;
					for (int m = 0; m < rootPoint.size(); m++) {
						int curDis = point.colorDistance(rootPoint.get(m).point);
						if (curDis < dis) {
							dis = curDis;
							index = m;
						}
					}
					rootPoint.get(index).addPoint(point);
					count++;
				}
			// 查看是新的种子点平均RGB是否等于原来的种子点，如果有，则说明收敛完毕
			if (isEnd(rootPoint)) {
				break;
			}
		} while (count < maxIterater);
		
		List<Point> pointList = getThreeFamilly(rootPoint);
		List<String> list = new ArrayList<>();
		for (int i = 0; i < pointList.size() && i<colorNum; i++) {
			list.add( pointList.get(i).toString() );
		}
		return list;
	}

	public static void main(String[] args){
//		File file = new File("E:/icon_okr72x.png");
//		BufferedImage bi = null;
//		try {
//			bi = ImageIO.read(file);
//			ImageMainColorUtil ImageUtil = new ImageMainColorUtil();
//			List<String> list = ImageUtil.getColorSolution( bi, 30, 1);
//			if( list != null && !list.isEmpty() ){
//				list.forEach( s -> System.out.println(s));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
}