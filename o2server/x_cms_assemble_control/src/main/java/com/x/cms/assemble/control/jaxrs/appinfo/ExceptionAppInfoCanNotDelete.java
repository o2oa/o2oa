package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoCanNotDelete extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoCanNotDelete(Long count) {
		super("该应用栏目内仍存在{}个分类，请删除分类后再删除栏目信息.", count);
	}
}
