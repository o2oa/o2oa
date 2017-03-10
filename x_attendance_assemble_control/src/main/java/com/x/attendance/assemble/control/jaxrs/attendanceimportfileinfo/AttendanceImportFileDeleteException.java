package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceImportFileDeleteException( Throwable e, String id ) {
		super("系统根据ID删除人员考勤数据导入文件对象信息时发生异常.ID:" + id, e );
	}
}
