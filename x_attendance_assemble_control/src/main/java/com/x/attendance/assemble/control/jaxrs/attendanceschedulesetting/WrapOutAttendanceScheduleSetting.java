package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceScheduleSetting.class )
public class WrapOutAttendanceScheduleSetting extends AttendanceScheduleSetting  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}