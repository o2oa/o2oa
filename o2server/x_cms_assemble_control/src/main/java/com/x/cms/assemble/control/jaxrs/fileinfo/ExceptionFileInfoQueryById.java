package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFileInfoQueryById extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoQueryById( Throwable e, String id ) {
		super("系统根据指定的ID查询文件信息时发生异常。Id:{}", id, e );
	}
}
