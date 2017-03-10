package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import com.x.base.core.exception.PromptException;

class AttendanceWorkDayConfigNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkDayConfigNotExistsException( String id ) {
		super("指定ID的节假日工作日配置信息对象不存在.ID:" + id );
	}
}
