package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceImportFileQueryByIdException( Throwable e, String id ) {
		super("根据指定ID查询导入文件信息时发生异常.ID:" + id, e );
	}
}
