package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileReadException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceImportFileReadException( Throwable e, String id, String name ) {
		super("解析本地Excle文件时发生异常.ID:" + id + ", FileName:" + name, e );
	}
}
