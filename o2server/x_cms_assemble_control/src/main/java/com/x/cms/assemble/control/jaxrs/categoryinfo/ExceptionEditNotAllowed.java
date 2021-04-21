package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEditNotAllowed extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEditNotAllowed(Long count) {
		super( "该分类中仍有{}个文档，请删除所有文档后再删除分类信息.", count);
	}
}
