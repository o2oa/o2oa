package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.project.exception.PromptException;

class ExceptionSelfHolidayNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSelfHolidayNotExists( String id ) {
		super("指定ID的员工个人请假申请信息对象不存在.ID:" + id );
	}
}
