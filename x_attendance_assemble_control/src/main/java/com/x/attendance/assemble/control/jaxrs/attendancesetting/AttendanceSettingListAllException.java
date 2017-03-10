package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class AttendanceSettingListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSettingListAllException( Throwable e ) {
		super("系统查询所有考勤系统设置信息列表时发生异常.", e );
	}
}
