package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceImportFileQueryByIdException( Throwable e, String id ) {
		super("系统根据ID查询指定的人员考勤数据导入文件信息时发生异常.ID:" + id );
	}
}
