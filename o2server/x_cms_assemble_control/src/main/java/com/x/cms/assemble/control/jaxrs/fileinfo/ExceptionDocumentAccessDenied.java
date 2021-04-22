package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentAccessDenied extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionDocumentAccessDenied(String person, String title, String docId) {
		super("用户:{} 没有权限访问文档 name: {} id: {}.", person, title, docId);
	}

}
