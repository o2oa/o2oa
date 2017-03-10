package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class GetAttendanceSettingByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetAttendanceSettingByIdException( Throwable e, String id ) {
		super("系统根据ID查询指定考勤系统配置信息时发生异常.ID:" + id, e );
	}
}
