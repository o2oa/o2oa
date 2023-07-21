package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.attendance.assemble.control.service.AttendanceImportFileInfoServiceAdv;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{

	protected AttendanceImportFileInfoServiceAdv attendanceImportFileInfoServiceAdv = new AttendanceImportFileInfoServiceAdv();

}
