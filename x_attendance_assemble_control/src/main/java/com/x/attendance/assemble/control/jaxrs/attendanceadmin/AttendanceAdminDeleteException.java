package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.base.core.exception.PromptException;

class AttendanceAdminDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAdminDeleteException( Throwable e, String id ) {
		super("系统在根据ID删除管理员信息时发生异常.ID:" + id, e );
	}
}
