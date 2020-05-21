package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionCheckImportData extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public ExceptionCheckImportData(Throwable e) {
		super("检查导入数据信息时发生异常!", e);
	}
}
