package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.project.exception.LanguagePromptException;

class PortalInsufficientPermissionException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalInsufficientPermissionException(String person, String name, String id) {
		super("用户: {} 没有足够的权限操作站点: {}, id:{}.", person, name, id);
	}
}
