package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionSingleDetailNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSingleDetailNotExists( ) {
		super("根据人员和打卡日期查找员工打卡信息不存在！" );
	}
}
