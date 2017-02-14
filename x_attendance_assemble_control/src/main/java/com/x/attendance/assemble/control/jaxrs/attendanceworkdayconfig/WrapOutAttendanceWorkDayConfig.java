package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceWorkDayConfig.class )
public class WrapOutAttendanceWorkDayConfig extends AttendanceWorkDayConfig  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}