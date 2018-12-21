package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppIdEmpty() {
		super("分类信息中“所属应用栏目ID”不能为空。" );
	}
}
