package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceAppealInfo.class )
public class WrapOutAttendanceAppealInfo extends AttendanceAppealInfo  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}