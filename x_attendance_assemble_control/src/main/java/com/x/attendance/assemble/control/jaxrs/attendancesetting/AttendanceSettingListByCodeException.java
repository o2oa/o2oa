package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class AttendanceSettingListByCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSettingListByCodeException( Throwable e, String code ) {
		super("系统根据指定的编码'CODE'查询所有符合条件的考勤系统设置信息列表时发生异常.Code:" + code, e );
	}
}
