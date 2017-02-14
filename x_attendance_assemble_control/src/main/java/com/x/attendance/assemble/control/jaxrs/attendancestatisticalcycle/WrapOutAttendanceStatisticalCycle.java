package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceStatisticalCycle.class )
public class WrapOutAttendanceStatisticalCycle extends AttendanceStatisticalCycle  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}