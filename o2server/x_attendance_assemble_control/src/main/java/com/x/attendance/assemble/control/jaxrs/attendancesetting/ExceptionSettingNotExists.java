package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSettingNotExists( String id ) {
		super("指定的考勤系统配置信息不存在.ID:" + id );
	}
}
