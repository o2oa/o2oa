package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkDayConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDayConfigNotExists( String id ) {
		super("指定ID的节假日工作日配置信息对象不存在.ID:" + id );
	}
}
