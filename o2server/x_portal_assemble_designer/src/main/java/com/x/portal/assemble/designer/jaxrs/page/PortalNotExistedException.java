package com.x.portal.assemble.designer.jaxrs.page;

import com.x.base.core.project.exception.LanguagePromptException;

class PortalNotExistedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalNotExistedException(String id) {
		super("指定的站点不存在:{}.", id);
	}
}
