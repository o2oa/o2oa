package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyExtension extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyExtension(String name) {
		super("不能上传文件扩展名为空的文件: {}.", name);
	}
}
