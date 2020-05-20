package com.x.cms.common.image.maincolor;

/**
 * 点群定义
 *
 */
public class PointGroup {
	int sumaryR = 0;
	int sumaryG = 0;
	int sumaryB = 0;
	int pointCount = 0;
	Point point;

	public void addPoint(Point point) {
		this.sumaryR += point.R;
		this.sumaryG += point.G;
		this.sumaryB += point.B;
		pointCount++;
	}

	public Point getNewRGB() {
		Point newpoint = new Point();
		if (pointCount != 0) {
			newpoint.R = sumaryR / pointCount;
			newpoint.G = sumaryG / pointCount;
			newpoint.B = sumaryB / pointCount;
		} else {
			newpoint = this.point;
		}
		return newpoint;
	}
}