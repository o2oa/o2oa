package com.x.cms.common.image.maincolor;

/**
 * 定义RGB像素点
 *
 */
public class Point {
	int R;
	int G;
	int B;

	Point() {
	}

	public Point(int r, int g, int b) {
		super();
		R = r;
		G = g;
		B = b;
	}

	public int getR() {
		return R;
	}

	public void setR(int r) {
		R = r;
	}

	public int getG() {
		return G;
	}

	public void setG(int g) {
		G = g;
	}

	public int getB() {
		return B;
	}

	public void setB(int b) {
		B = b;
	}

	public int colorDistance(Point point) {
		int absR = this.R - point.R;
		int absG = this.G - point.G;
		int absB = this.B - point.B;
		int sqrt = (int) Math.sqrt(absR * absR + absG * absG + absB * absB);
		return sqrt;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	};

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		Point point = (Point) obj;
		if (this.R == point.R && this.G == point.G && this.B == point.B) {
			result = true;
		}
		return result;
	}

	public void add(Point point) {
		this.R += point.R;
		this.G += point.G;
		this.B += point.B;
	}

	@Override
	public String toString() {
		return this.R + "," + this.G + "," + this.B;
	}
}