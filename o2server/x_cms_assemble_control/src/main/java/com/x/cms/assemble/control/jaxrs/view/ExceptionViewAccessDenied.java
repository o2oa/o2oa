package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewAccessDenied(String categoryId, String viewId) {
		super("无权限访问视图id: {} 分类id: {}.", categoryId, viewId );
	}
}
