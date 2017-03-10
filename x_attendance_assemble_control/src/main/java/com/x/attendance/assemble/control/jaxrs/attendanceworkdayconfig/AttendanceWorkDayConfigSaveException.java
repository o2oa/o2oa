package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import com.x.base.core.exception.PromptException;

class AttendanceWorkDayConfigSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkDayConfigSaveException(Exception e ) {
		super("系统保存节假日工作日配置信息对象时发生异常.", e );
	}
}
