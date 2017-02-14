package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceEmployeeConfig.class )
public class WrapOutAttendanceEmployeeConfig extends AttendanceEmployeeConfig  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}