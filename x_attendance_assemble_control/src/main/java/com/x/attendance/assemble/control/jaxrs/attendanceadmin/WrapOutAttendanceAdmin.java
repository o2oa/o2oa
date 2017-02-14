package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceAdmin.class )
public class WrapOutAttendanceAdmin extends AttendanceAdmin  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}