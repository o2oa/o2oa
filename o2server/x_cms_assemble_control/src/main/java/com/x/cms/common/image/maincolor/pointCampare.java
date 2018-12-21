package com.x.cms.common.image.maincolor;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
class pointCampare implements Comparator {
	@Override
	public int compare(Object o1, Object o2) {
		PointGroup p1 = (PointGroup) o1;
		PointGroup p2 = (PointGroup) o2;
		if ((p1.point.R + p1.point.G + p1.point.B) >= (p2.point.R + p2.point.G + p2.point.B)) {
			return 1;
		} else {
			return 0;
		}
	}

}