package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileImportProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileImportProcess( Throwable e, String message ) {
		super("用户在进行考勤数据处理时发生异常！message:" + message, e );
	}
}
