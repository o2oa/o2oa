package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceSetting.class)
public class WrapInAttendanceSetting extends AttendanceSetting {
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