package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.attendance.assemble.control.service.AttendanceWorkPlaceServiceAdv;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{

	protected AttendanceWorkPlaceServiceAdv attendanceWorkPlaceServiceAdv = new AttendanceWorkPlaceServiceAdv();
	

}
