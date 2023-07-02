package com.x.general.assemble.control.jaxrs.office;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnsupportType extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionUnsupportType(String type) {
		super("不支持的文件格式: {}.", type);
	}
}
