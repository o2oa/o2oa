package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class AttendanceSettingDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSettingDeleteException( Throwable e, String id ) {
		super("根据ID删除考勤系统配置信息时发生异常.ID:"+id, e );
	}
}
