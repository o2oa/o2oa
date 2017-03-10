package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class AttendanceSettingWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSettingWrapOutException( Throwable e ) {
		super("将所有查询到的考勤配置信息对象转换为可以输出的信息时发生异常.", e );
	}
}
