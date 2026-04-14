package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyExtension extends PromptException {

	private static final long serialVersionUID = -8278882333321617294L;

	ExceptionEmptyExtension(String name) {
		super("不能上传文件扩展名为空的文件: {}.", name);
	}
}
