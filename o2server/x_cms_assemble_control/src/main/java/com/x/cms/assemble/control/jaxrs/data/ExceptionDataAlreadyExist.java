package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDataAlreadyExist extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionDataAlreadyExist(String title, String docId) {
		super("文档 title:{} id:{} 的数据已存在.", title, docId );
	}
}
