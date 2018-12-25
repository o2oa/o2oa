package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionPersonNotExists( String id ) {
		super("ID为{}的应用栏目分类管理员配置信息不存在。", id );
	}
}
