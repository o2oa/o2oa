package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileWriteToLocalException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceImportFileWriteToLocalException( Throwable e, String id, String name ) {
		super("将文件写入到本地文件时发生异常.ID:" + id + ", FileName:" + name, e );
	}

	public AttendanceImportFileWriteToLocalException(Exception e) {
		super("将文件写入到本地文件时发生异常.", e );
	}
}
