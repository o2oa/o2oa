package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class AttendanceSettingCodeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSettingCodeEmptyException() {
		super("查询操作传入的配置编码Code为空，无法进行查询或者保存操作.");
	}
}
