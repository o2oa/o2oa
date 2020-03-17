package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoNotExists( String id ) {
		super("指定的分类信息不存在。ID:" + id );
	}
}
