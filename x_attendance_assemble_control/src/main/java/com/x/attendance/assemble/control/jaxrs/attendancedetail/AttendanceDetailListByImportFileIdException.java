package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailListByImportFileIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailListByImportFileIdException( Throwable e, String fileId ) {
		super("系统在根据打卡信息导入文件ID查询员工打卡信息时发生异常！FileId:" + fileId, e );
	}
}
