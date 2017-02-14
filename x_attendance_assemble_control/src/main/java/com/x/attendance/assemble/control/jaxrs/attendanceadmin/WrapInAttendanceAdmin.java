package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceAdmin.class)
public class WrapInAttendanceAdmin extends AttendanceAdmin {
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

	private String identity = null;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}