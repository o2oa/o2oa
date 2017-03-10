package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailImportFileIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailImportFileIdEmptyException() {
		super("员工打卡记录导入文件ID为空，无法进行数据查询." );
	}
}
