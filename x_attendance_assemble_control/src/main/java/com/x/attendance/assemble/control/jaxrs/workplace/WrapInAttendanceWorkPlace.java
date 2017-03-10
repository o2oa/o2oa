package com.x.attendance.assemble.control.jaxrs.workplace;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceWorkPlace.class)
public class WrapInAttendanceWorkPlace extends AttendanceWorkPlace {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

}