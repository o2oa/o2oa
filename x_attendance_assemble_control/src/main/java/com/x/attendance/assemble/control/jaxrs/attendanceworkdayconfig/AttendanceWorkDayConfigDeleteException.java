package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import com.x.base.core.exception.PromptException;

class AttendanceWorkDayConfigDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkDayConfigDeleteException(Exception e, String id ) {
		super("系统保存节假日工作日配置信息对象时发生异常.ID:"+id, e );
	}
}
