package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyExtension extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public ExceptionEmptyExtension(String name) {
		super("不能上传文件扩展名为空的文件: {}.", name);
	}
}
