package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDocumentPermissionProcess extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentPermissionProcess() {
		super("文档权限管理时出现异常。");
	}
}
