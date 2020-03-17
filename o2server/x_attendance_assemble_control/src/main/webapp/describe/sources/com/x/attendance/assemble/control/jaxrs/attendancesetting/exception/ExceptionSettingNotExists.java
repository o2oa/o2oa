package com.x.attendance.assemble.control.jaxrs.attendancesetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSettingNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSettingNotExists( String id ) {
		super("指定的考勤系统配置信息不存在.ID:" + id );
	}
}
