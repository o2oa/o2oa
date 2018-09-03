package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionPortalAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionPortalAccessDenied(String person, String name, String id) {
		super("用户:{} 访问站点 name: {} id: {}, 被拒绝.", person, name, id);
	}

}
