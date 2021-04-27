package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFileInfoNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoNotExists( String id ) {
		super("指定的文件不存在:{}.", id );
	}
}
