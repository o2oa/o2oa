package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceImportFileListAllException( Throwable e ) {
		super("系统查询所有员工考勤数据导入文件信息时发生异常.", e );
	}
}
