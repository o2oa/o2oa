package test.opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TestClient {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source, template;
		// 将文件读入为OpenCV的Mat格式
		// source = Highgui.imread("/Users/niwei/Downloads/原图.jpeg");
		// template = Highgui.imread("/Users/niwei/Downloads/模板.jpeg");
		source = Imgcodecs.imread("d:/all.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		template = Imgcodecs.imread("d:/queding.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

		// 创建于原图相同的大小，储存匹配度
		Mat result = Mat.zeros(source.rows() - template.rows() + 1, source.cols() - template.cols() + 1,
				CvType.CV_32FC1);
		// 调用模板匹配方法
		Imgproc.matchTemplate(source, template, result, Imgproc.TM_SQDIFF_NORMED);
		// 规格化
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1);
		// 获得最可能点，MinMaxLocResult是其数据格式，包括了最大、最小点的位置x、y
		Core.MinMaxLocResult mlr = Core.minMaxLoc(result);
		Point matchLoc = mlr.minLoc;
		System.out.println(matchLoc);
		// 在原图上的对应模板可能位置画一个绿色矩形
		Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.width(), matchLoc.y + template.height()),
				new Scalar(0, 255, 0));
		// 将结果输出到对应位置
		Imgcodecs.imwrite("d:/b.png", source);
	}
}