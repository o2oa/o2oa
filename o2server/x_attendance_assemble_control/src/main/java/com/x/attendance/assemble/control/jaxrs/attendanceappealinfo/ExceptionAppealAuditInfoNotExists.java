package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppealAuditInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAppealAuditInfoNotExists(String id ) {
		super("员工打卡申诉审批信息不存在！ID:" + id );
	}
}
