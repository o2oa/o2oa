package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.exception.PromptException;

class ExceptionExcelResultObject extends PromptException {

	private static final long serialVersionUID = 5816811446977738267L;

	ExceptionExcelResultObject(String flag) {
		super("导出的文件不存在:{}.", flag);
	}

}
