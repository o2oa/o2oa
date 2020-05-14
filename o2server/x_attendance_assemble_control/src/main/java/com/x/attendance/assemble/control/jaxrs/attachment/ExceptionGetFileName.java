package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetFileName extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public ExceptionGetFileName(Throwable e) {
		super("获取上传的文件名时发生异常!", e);
	}
}
