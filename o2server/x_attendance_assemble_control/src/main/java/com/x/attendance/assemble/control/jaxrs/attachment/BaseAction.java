package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.AttendanceImportFileInfoServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction {
	protected AttendanceImportFileInfoServiceAdv importFileInfoServiceAdv = new AttendanceImportFileInfoServiceAdv();
	protected AttendanceScheduleSettingServiceAdv attendanceScheduleSettingServiceAdv = new AttendanceScheduleSettingServiceAdv();
	protected DateOperation dateOperation = new DateOperation();
}

