package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryViewAppIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionQueryViewAppIdEmpty() {
		super("视图信息中应用栏目ID为空，无法保存视图信息。" );
	}
}
