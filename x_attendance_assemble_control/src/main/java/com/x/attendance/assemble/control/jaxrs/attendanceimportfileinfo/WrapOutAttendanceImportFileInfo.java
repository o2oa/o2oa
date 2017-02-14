package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceImportFileInfo.class )
public class WrapOutAttendanceImportFileInfo extends AttendanceImportFileInfo  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
	static {
		Excludes.add("fileBody");
		Excludes.add("dataContent");
	}
}