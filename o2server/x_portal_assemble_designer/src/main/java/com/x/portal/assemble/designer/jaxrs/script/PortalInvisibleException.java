package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class PortalInvisibleException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalInvisibleException(String person, String name, String id) {
		super("用户: {} 无权限访问此站点 name: {} id: {}.", person, name, id);
	}
}
