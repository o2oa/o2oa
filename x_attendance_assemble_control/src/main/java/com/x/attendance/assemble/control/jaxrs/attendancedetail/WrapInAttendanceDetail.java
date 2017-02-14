package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceDetail.class)
public class WrapInAttendanceDetail extends AttendanceDetail {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

}