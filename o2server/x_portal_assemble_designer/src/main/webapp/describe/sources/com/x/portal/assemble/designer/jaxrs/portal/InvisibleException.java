package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.project.exception.PromptException;

class InvisibleException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	InvisibleException(String person, String name, String id) {
		super("用户: {} 不可见站点: {} id: {}.", person, name, id);
	}
}
