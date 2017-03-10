package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileImportException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public AttendanceImportFileImportException( Throwable e, String id ) {
		super("数据导入时发生未知异常.ID:" + id, e );
	}
}
