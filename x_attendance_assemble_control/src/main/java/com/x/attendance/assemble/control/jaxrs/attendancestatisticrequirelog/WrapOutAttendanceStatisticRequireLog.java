package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceStatisticRequireLog.class )
public class WrapOutAttendanceStatisticRequireLog extends AttendanceStatisticRequireLog  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}