package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceDetailMobile.class )
public class WrapOutAttendanceDetailMobile extends AttendanceDetailMobile  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}