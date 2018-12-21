package com.x.query.assemble.designer.jaxrs.query;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionQueryAccessDenied(String person, String name, String id) {
		super("用户:{} 访问查询:{}, id: {}, 权限不足.", person, name, id);
	}

}
